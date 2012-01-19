;
(function ($) {

    var TreeViewer = function ($this, base, ref, path, rootName) {
        var that = this;
        this.target = $this;
        this.base = base;
        this.ref = ref;
        this.path = path;
        this.rootName = rootName;
        this.nav = $('<ul>').addClass('breadcrumb').appendTo(this.target);
        this.table = $('<table>').addClass('bordered-table').appendTo(this.target);
        window.addEventListener('popstate', function(event) {
            if(location.pathname.match(/^(.+?\/)?.+?\/.+?\/tree/)){
                var treePath = location.pathname.substr(location.pathname.indexOf('tree/') + 'tree/'.length);
                treePath.match(/(.+?)\/(.*$)/);
//                that.ref = RegExp.$1;
                that.path = RegExp.$2;
            } else {
                that.path = "";
            }
            that.renderAsBack(false);
        }, false);
    };

    TreeViewer.prototype.init = function () {
        this.getTree();
    }

    TreeViewer.prototype.renderNav = function () {
        var that = this;
        that.nav.empty();

        var $li = $('<li>').appendTo(that.nav);
        if (!that.path || that.path.length === 0) {
            $li.text(that.rootName).addClass('active');
            return;
        }

        var paths = that.path.split('/');

        $('<a>').attr('href', that.makePath(that.rootName)).text(that.rootName).click(
            function (e) {
                e.preventDefault();
                that.path = "";
                that.renderAsBack();
            }).appendTo($li);
        $li.append($('<span>').addClass('divider').text('/'));

        var tmpPath = "";
        for (var i = 0; i < paths.length; i++) {
            (function (rootPath, path) {
                var newPath = rootPath ? rootPath + "/" + path : path;
                tmpPath = newPath;
                var $item = $('<li>');
                if ((i + 1) == paths.length) {
                    $item.text(path).addClass('active').appendTo(that.nav);
                    return;
                }
                $('<a>').attr('href', that.makePath(newPath)).text(path).click(
                    function (e) {
                        e.preventDefault();
                        that.path = newPath;
                        that.renderAsBack();
                    }).appendTo($item);
                $item.append($('<span>').addClass('divider').text('/')).appendTo(that.nav);
            })(tmpPath, paths[i]);
        }
    }

    TreeViewer.prototype.pushHistory = function (pushHistory) {
        pushHistory = pushHistory === undefined ? true : pushHistory;
        if (pushHistory) {
            window.history.pushState(null, null, this.makePath());
        }
    }

    TreeViewer.prototype.getTree = function () {
        var that = this;
        $.ajax({
            type:"get",
            dataType:"json",
            url:that.makePath(),
            success:function (data) {
                that.render(data);
            }
        });
    }

    TreeViewer.prototype.renderAsBack = function (pushHistory) {
        var that = this;
        this.pushHistory(pushHistory);
        this.table.effect('slide', { direction:"right", mode:'hide' }, 200, function () {
            that.table.empty();
            that.getTree();
        });
    }

    TreeViewer.prototype.renderAsForward = function (pushHistory) {
        var that = this;
        this.pushHistory(pushHistory);
        this.table.effect('slide', { direction:"left", mode:'hide' }, 200, function () {
            that.table.empty();
            that.getTree();
        });
    }

    TreeViewer.prototype.render = function (data) {
        var parent = data.parent;
        var files = data.files;

        var $thead = $('<thead>').appendTo(this.table);
        $('<tr>')
            .append($('<th>').text(''))
            .append($('<th>').text('name'))
            .append($('<th>').text('age'))
            .append($('<th>').text('message'))
            .append($('<th>').text('history')).appendTo($thead);

        var $tbody = $('<tbody>').appendTo(this.table);

        var $tr = $('<tr>').appendTo($tbody);
        this.renderParent(parent, $tr);

        for (var i = 0; i < files.length; i++) {
            $tr = $('<tr>').appendTo($tbody);
            if (files[i].type === 'tree') {
                this.renderTree(files[i], $tr);
            } else if (files[i].type === 'blob') {
                this.renderBlob(files[i], $tr);
            }
        }
        this.target.append(this.table);
        this.table.show();
        this.renderNav();
    }

    TreeViewer.prototype.renderParent = function (parent, $tr) {
        var that = this;
        if (parent || parent === "") {
            $('<td>').appendTo($tr);
            $('<td>').append($('<a>').text('..').attr('href', that.makePath(parent)).click(function (e) {
                e.preventDefault();
                that.path = parent;
                that.renderAsBack();
            })).appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
        }
    }

    TreeViewer.prototype.makePath = function (path) {
        path = path || this.path;
        return this.base + "/" + this.ref + "/" + path;
    }

    TreeViewer.prototype.renderBlob = function (file, $tr) {
        (function (file, $tr) {
            $('<td>').text(file.type).appendTo($tr);
            $('<td>').text(file.name).appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
        })(file, $tr);
    }

    TreeViewer.prototype.renderTree = function (file, $tr) {
        var that = this;
        (function (file, $tr) {
            var newPath = that.path ? that.path + "/" + file.name : file.name;
            $('<td>').text(file.type).appendTo($tr);
            $('<td>').append($('<a>').text(file.name).attr('href', that.makePath(newPath)).click(function (e) {
                e.preventDefault();
                that.path = newPath;
                that.renderAsForward();
            })).appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
        })(file, $tr);
    }

    $.fn.gitTree = function (url, ref, options) {

        var opts = $.extend({}, $.fn.gitTree.defaults, options);

        return this.each(function () {
            var viewer = new TreeViewer($(this), url, ref, opts.path, opts.rootName);
            viewer.init();
        });
    };

    $.fn.gitTree.defaults = {
        path:"",
        rootName:"root"
    }

})(jQuery);