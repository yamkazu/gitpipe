;
(function ($) {

    var TreeViewer = function ($this) {
        var that = this;
        this.target = $this;

        this.nav = $('<ul>').addClass('breadcrumb').appendTo(this.target);
        this.content = $('<div>').appendTo(this.target);

        this.nextDirection = "";

        window.addEventListener('popstate', function (event) {
            that.hideContentAsBack(function () {
                var type = event.state;
                var data = that.target.data(location.pathname);
                if (type === "tree") {
                    that.renderTree(data);
                } else if (type === "blob") {
                    that.renderBlob(data);
                }
            });
        }, false);
    };

    $.extend(TreeViewer.prototype, {
        renderNav:function (currnet, parents) {
            var that = this;
            that.nav.empty();

            if (parents) {
                for (var i = 0; i < parents.length; i++) {
                    (function (parent) {
                        that.renderNavPath(parent);
                    })(parents[i]);
                }
            }

            that.renderNavPath(currnet, true);
        },

        renderNavPath:function (pathInfo, current) {
            var $path = $('<li>');
            var that = this;
            if (current) {
                $path.text(pathInfo.name).addClass('active').appendTo(that.nav);
            } else {
                $('<a>').attr('href', pathInfo.url).text(pathInfo.name).click(
                    function (e) {
                        e.preventDefault();
                        that.hideContentAsBack(function () {
                            that.getTree(pathInfo.url);
                        });
                    }).appendTo($path);
                $path.append($('<span>').addClass('divider').text('/')).appendTo(that.nav);
            }
        },

        pushHistory:function (type, url, pushState) {
            pushState = pushState === undefined ? true : pushState;
            if (pushState) {
                window.history.pushState(type, null, url);
            } else {
                window.history.replaceState(type, null, url);
            }
        },

        getTree:function (url, pushUrl, pushState) {
            pushUrl = pushUrl || url;
            var that = this;

            var cache = that.target.data(pushUrl);
            if (cache) {
                that.pushHistory("tree", pushUrl, pushState);
                that.renderTree(cache);
                return;
            }

            $.ajax({
                type:"get",
                dataType:"json",
                url:url,
                success:function (data) {
                    that.target.data(pushUrl, data);
                    if (pushUrl !== url) {
                        that.target.data(url, data);
                    }
                    that.pushHistory("tree", pushUrl, pushState);
                    that.renderTree(data);
                }
            });
        },

        getBlob:function (url, pushUrl, pushState) {
            pushUrl = pushUrl || url;
            var that = this;

            var cache = that.target.data(pushUrl);
            if (cache) {
                that.pushHistory("blob", pushUrl, pushState);
                that.renderBlob(cache);
                return;
            }

            $.ajax({
                type:"get",
                dataType:"json",
                url:url,
                success:function (data) {
                    that.target.data(pushUrl, data);
                    if (pushUrl !== url) {
                        that.target.data(url, data);
                    }
                    that.pushHistory("blob", pushUrl, pushState);
                    that.renderBlob(data);
                }
            });
        },

        getBlame:function (url, pushUrl, pushState) {
            pushUrl = pushUrl || url;
            var that = this;

            var cache = that.target.data(pushUrl);
            if (cache) {
                that.pushHistory("blame", pushUrl, pushState);
                that.renderBlob(cache);
                return;
            }

            $.ajax({
                type:"get",
                dataType:"json",
                url:url,
                success:function (data) {
                    that.target.data(pushUrl, data);
                    if (pushUrl !== url) {
                        that.target.data(url, data);
                    }
                    that.pushHistory("blame", pushUrl, pushState);
                    that.renderBlame(data);
                }
            });
        },


        renderBlob:function (data) {
            this.content.append(this.renderBlobInfo(data));

            if (data.file_type === 'binary') {
                this.content.append($('<div>').addClass('well').text('no viewer'));
                this.slideContent("show");
                this.renderNav(data.current, data.parents);
                return;
            }

            var brush = new SyntaxHighlighter.brushes[data.file_type]();

            brush.init({toolbar:false});
            var html = brush.getHtml(data.data);

            var $code = $(html);
            $('.code div', $code).removeClass('container');

            this.content.append($code);
            this.slideContent("show");
            this.renderNav(data.current, data.parents);
        },

        renderBlame:function (data) {
            this.content.append(this.renderBlameInfo(data));
            var brush = new SyntaxHighlighter.brushes[data.raw.type]();

            brush.init({toolbar:false});
            var html = brush.getHtml(data.raw.file);

            var $code = $(html);
            $('.code div', $code).removeClass('container');

            var $tr = $('tr', $code)
            var $td = $('<td>').addClass('blame').prependTo($tr);

            var entries = data.entries;
            var current = 0;
            for (var i = 0; i < entries.length; i++) {
                var entry = entries[i];
                for (var j = 0; j < entry.length; j++) {
                    var $line = $('<div>').addClass('line').addClass('number' + (current + 1)).addClass('index' + current).appendTo($td);
                    if (j == 0) {
                        $line.append($('<time>').text(entry.commit.date));
                        $line.append($('<a>').attr('href', entry.commit.url).text(entry.commit.id.substr(0, 10)));
                        $line.append($('<span>').text('»'));
                        if (entry.commit.author.username) {
                            $line.append($('<a>').attr('href', entry.commit.author.url).text(entry.commit.author.username));
                        } else {
                            $line.append($('<span>').text(entry.commit.author.name));
                        }
                    } else {
                        $line.text(' ');
                    }
                    current++;
                }
                if (data.size != current) {
                    $('.number' + current, $code).addClass('separate');
                }
            }
            this.content.append($code);
            this.slideContent("show");
            this.renderNav(data.current, data.parents);
        },

        renderBlobInfo:function (data) {
            var $info = $('<div>').addClass('blob-info');
            $('<span>').text(data.mode).appendTo($info);
            $('<span>').text(data.size + ' kb').appendTo($info);
            var $actions = $('<div>').addClass('pull-right').appendTo($info);
            $actions.append($('<a>').attr('href', data.historyUrl).text('history'));
            $actions.append($('<a>').attr('href', data.blameUrl).text('blame'));
            $actions.append($('<a>').attr('href', data.rawUrl).text('raw'));
            return $info;
        },

        renderBlameInfo:function (data) {
            var $info = $('<div>').addClass('blob-info');
            $('<span>').text(data.raw.mode).appendTo($info);
            $('<span>').text(data.raw.size + ' kb').appendTo($info);
            var $actions = $('<div>').addClass('pull-right').appendTo($info);
            $actions.append($('<a>').attr('href', data.historyUrl).text('history'));
            $actions.append($('<a>').attr('href', data.current.url).text('normal view'));
            $actions.append($('<a>').attr('href', data.rawUrl).text('raw'));
            return $info;
        },

        hideContentAsBack:function (callback) {
            var that = this;
            callback = callback || function () {
            };
            this.nextDirection = "left"
            this.slideContent("hide", "right", function () {
                that.content.empty();
                callback();
            });
        },

        hideContentAsForward:function (callback) {
            var that = this;
            callback = callback || function () {
            };
            this.nextDirection = "right"
            this.slideContent("hide", "left", function () {
                that.content.empty();
                callback();
            });
        },

        renderTree:function (data) {
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
                .append($('<th>').append($('<a>').attr('href', data.historyUrl).text('history'))).appendTo($thead);

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
            this.renderNav(data.current, data.parents);
        },

        slideContent:function (mode, direction, callback) {
            direction = direction || this.nextDirection
            if (!direction || direction === '') {
                this.content.show();
                return;
            }
            this.content.effect('slide', { mode:mode, direction:direction }, 200, callback);
        },

        renderTrAsParent:function (parent, $tr) {
            var that = this;
            if (parent) {
                $('<td>').appendTo($tr);
                $('<td>').append($('<a>').text('..').attr('href', parent).click(function (e) {
                    e.preventDefault();
                    that.hideContentAsBack(function () {
                        that.getTree(parent);
                    });
                })).appendTo($tr);
                $('<td>').appendTo($tr);
                $('<td>').appendTo($tr);
                $('<td>').appendTo($tr);
            }
        },

        renderTrAsBlob:function (current, file, $tr) {
            var that = this;
            (function (current, file, $tr) {
                $('<td>').text(file.type).appendTo($tr);
                $('<td>').append($('<a>').text(file.name).attr('href', file.url).click(function (e) {
                    e.preventDefault();
                    that.hideContentAsForward(function () {
                        that.getBlob(file.url);
                    });
                })).appendTo($tr);
                $('<td>').text(file.commit.date).appendTo($tr);
                var message = $('<td>').append($('<a>').attr('href', file.commit.url).text(file.commit.shortMessage)).appendTo($tr);
                if (file.commit.author.username) {
                    message.append('&nbsp;')
                    $('<a>').attr('href', file.commit.author.url).text('[' + file.commit.author.username + ']').appendTo(message);
                }
                $('<td>').appendTo($tr);
            })(current, file, $tr);
        },

        renderTrAsTree:function (current, file, $tr) {
            var that = this;
            (function (current, file, $tr) {
                $('<td>').text(file.type).appendTo($tr);
                $('<td>').append($('<a>').text(file.name).attr('href', file.url).click(function (e) {
                    e.preventDefault();
                    that.hideContentAsForward(function () {
                        that.getTree(file.url);
                    });
                })).appendTo($tr);
                $('<td>').text(file.commit.date).appendTo($tr);
                var message = $('<td>').append($('<a>').attr('href', file.commit.url).text(file.commit.shortMessage)).appendTo($tr);
                if (file.commit.author.username) {
                    message.append('&nbsp;')
                    $('<a>').attr('href', file.commit.author.url).text('[' + file.commit.author.username + ']').appendTo(message);
                }
                $('<td>').appendTo($tr);
            })(current, file, $tr);
        }
    });


    $.fn.gitTree = function (url) {
        return this.each(function () {
            var viewer = new TreeViewer($(this));
            viewer.getTree(url, location.pathname, false);
        });
    };

    $.fn.gitBlob = function (url) {
        return this.each(function () {
            var viewer = new TreeViewer($(this));
            viewer.getBlob(url, location.pathname, false);
        });
    };

    $.fn.gitBlame = function (url) {
        return this.each(function () {
            var viewer = new TreeViewer($(this));
            viewer.getBlame(url, location.pathname, false);
        });
    };


})(jQuery);