;
(function ($) {

    var TreeViewer = function ($this, base, ref, rootName) {
        var that = this;
        this.target = $this;
        this.base = base;
        this.ref = ref;
        this.rootName = rootName;

        this.nav = $('<ul>').addClass('breadcrumb').appendTo(this.target);
        this.content = $('<div>').appendTo(this.target);

        this.nextDirection = "";

        window.addEventListener('popstate', function (event) {
            that.hideContentAsBack(function () {
//                if (location.pathname.match(/^(.+?\/)?.+?\/.+?\/tree/)) {
//                    var treePath = location.pathname.substr(location.pathname.indexOf('tree/') + 'tree/'.length);
//                    treePath.match(/(.+?)\/(.*$)/);
//                    that.path = RegExp.$2;
//                } else {
//                    that.path = "";
//                }
//                that.getTree();
                that.renderTree(event.state);
            });
        }, false);
    };

    TreeViewer.prototype.renderNav = function (path) {
        var that = this;
        that.nav.empty();

        var $li = $('<li>').appendTo(that.nav);
        if (!path || path.length === 0) {
            $li.text(that.rootName).addClass('active');
            return;
        }
        $('<a>').attr('href', that.createTreeLink("")).text(that.rootName).click(
            function (e) {
                e.preventDefault();
                that.hideContentAsBack(function () {
                    that.getTree(that.createTreeLink(""));
                });
            }).appendTo($li);
        $li.append($('<span>').addClass('divider').text('/'));

        var paths = path.split('/');
        var tmpPath = "";
        for (var i = 0; i < paths.length; i++) {
            (function (rootPath, path) {
                var newPath = rootPath ? rootPath + "/" + path : path;
                tmpPath = newPath;
                var $item = $('<li>');
                if ((i + 1) == paths.length) { // 末端要素
                    $item.text(path).addClass('active').appendTo(that.nav);
                    return;
                }
                $('<a>').attr('href', that.createTreeLink(newPath)).text(path).click(
                    function (e) {
                        e.preventDefault();
                        that.hideContentAsBack(function () {
                            that.getTree(that.createTreeLink(newPath));
                        });
                    }).appendTo($item);
                $item.append($('<span>').addClass('divider').text('/')).appendTo(that.nav);
            })(tmpPath, paths[i]);
        }
    }

    TreeViewer.prototype.pushHistory = function (url, data) {
        window.history.pushState(data, null, url);
    }

    TreeViewer.prototype.getTree = function (url) {
        var that = this;
        $.ajax({
            type:"get",
            dataType:"json",
            url:url,
            success:function (data) {
                that.pushHistory(url, data);
                that.renderTree(data);
            }
        });
    }

    TreeViewer.prototype.getBlob = function (url) {
        var that = this;
        $.ajax({
            type:"get",
            dataType:"json",
            url:url,
            success:function (data) {
                that.pushHistory(url, data);
                that.renderBlob(data);
            }
        });
    }

    TreeViewer.prototype.renderBlob = function (data) {
        var brush = new SyntaxHighlighter.brushes['Plain']();
        brush.init({toolbar:false});
        var html = brush.getHtml(data.data);

        var $code = $(html);
        $('.code div', $code).removeClass('container');
        this.content.append($code);
        this.slideContent("show");
        this.renderNav(data.path);
    }


    TreeViewer.prototype.hideContentAsBack = function (callback) {
        var that = this;
        callback = callback || function () {
        };
        this.nextDirection = "left"
        this.slideContent("hide", "right", function () {
            that.content.empty();
            callback();
        })
    }

    TreeViewer.prototype.hideContentAsForward = function (callback) {
        var that = this;
        callback = callback || function () {
        };
        this.nextDirection = "right"
        this.slideContent("hide", "left", function () {
            that.content.empty();
            callback();
        })
    }

    TreeViewer.prototype.slideContent = function (mode, direction, callback) {
        direction = direction || this.nextDirection
        if (!direction || direction === '') {
            this.content.show();
            return;
        }
        this.content.effect('slide', { mode:mode, direction:direction }, 200, callback);
    }

    TreeViewer.prototype.renderTree = function (data) {
        var parent = data.parent;
        var files = data.files;

        // table
        var $table = $('<table>').addClass('bordered-table');

        // thead
        var $thead = $('<thead>').appendTo($table);
        $('<tr>')
            .append($('<th>').text(''))
            .append($('<th>').text('name'))
            .append($('<th>').text('age'))
            .append($('<th>').text('message'))
            .append($('<th>').text('history')).appendTo($thead);

        // tbody
        var $tbody = $('<tbody>').appendTo($table);

        var $tr = $('<tr>').appendTo($tbody);
        this.renderTrAsParent(parent, $tr);

        for (var i = 0; i < files.length; i++) {
            $tr = $('<tr>').appendTo($tbody);
            if (files[i].type === 'tree') {
                this.renderTrAsTree(data.current, files[i], $tr);
            } else if (files[i].type === 'blob') {
                this.renderTrAsBlob(data.current, files[i], $tr);
            }
        }

        this.content.append($table);
        this.slideContent("show");
        this.renderNav(data.current);
    }

    TreeViewer.prototype.renderTrAsParent = function (parent, $tr) {
        var that = this;
        if (parent || parent === "") {
            $('<td>').appendTo($tr);
            $('<td>').append($('<a>').text('..').attr('href', that.createTreeLink(parent)).click(function (e) {
                e.preventDefault();
                that.hideContentAsBack(function () {
                    that.getTree(that.createTreeLink(parent));
                });
            })).appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
            $('<td>').appendTo($tr);
        }
    }


    TreeViewer.prototype.createTreeLink = function (path) {
        return this.base + "/tree/" + this.ref + "/" + path;
    }

    TreeViewer.prototype.createBlobLink = function (path) {
        return this.base + "/blob/" + this.ref + "/" + path;
    }

    TreeViewer.prototype.renderTrAsBlob = function (current, file, $tr) {
        var that = this;
        (function (current, file, $tr) {
            $('<td>').text(file.type).appendTo($tr);
            $('<td>').append($('<a>').text(file.name).attr('href', that.createBlobLink(current !== "" ? current + "/" + file.name : file.name)).click(function (e) {
                e.preventDefault();
                that.hideContentAsForward(function () {
                    that.getBlob(that.createBlobLink(current !== "" ? current + "/" + file.name : file.name));
                });
            })).appendTo($tr);
            $('<td>').text(file.date).appendTo($tr);
            $('<td>').text(file.message + '[' + file.author + ']').appendTo($tr);
            $('<td>').appendTo($tr);
        })(current, file, $tr);
    }

    TreeViewer.prototype.renderTrAsTree = function (current, file, $tr) {
        var that = this;
        (function (current, file, $tr) {
            $('<td>').text(file.type).appendTo($tr);
            $('<td>').append($('<a>').text(file.name).attr('href', that.createTreeLink(current !== "" ? current + "/" + file.name : file.name)).click(function (e) {
                e.preventDefault();
                that.hideContentAsForward(function () {
                    that.getTree(that.createTreeLink(current !== "" ? current + "/" + file.name : file.name));
                });
            })).appendTo($tr);
            $('<td>').text(file.date).appendTo($tr);
            var message = $('<td>').text(file.message).appendTo($tr);
            if (file.author) {
                $('<a>').text(' [' + file.author + ']').appendTo(message);
            }
            $('<td>').appendTo($tr);
        })(current, file, $tr);
    }

    $.fn.gitTree = function (url, ref, options) {
        var opts = $.extend({}, $.fn.gitTree.defaults, options);
        return this.each(function () {
            var viewer = new TreeViewer($(this), url, ref, opts.rootName);
            viewer.getTree(viewer.createTreeLink(opts.path));
        });
    };

    $.fn.gitTree.defaults = {
        path:"",
        rootName:"root"
    }

})(jQuery);