;
(function ($) {

    var TreeViewer = function ($this) {
        var that = this;
        this.target = $this;

        this.nav = $('<ul>').addClass('breadcrumb').appendTo(this.target);
        this.loading = $('<div>').addClass('loading').hide().appendTo(this.target);
        this.content = $('<div>').addClass('bubble tree-browser-wrapper').appendTo(this.target);

        this.nextDirection = "";
        this.eventType = "";

        // Prepare
        var History = window.History; // Note: We are using a capital H instead of a lower h
        if (!History.enabled) {
            // History.js is disabled for this browser.
            // This is because we can optionally choose to support HTML4 browsers or not.
            return false;
        }

        // Bind to StateChange Event
        History.Adapter.bind(window, 'statechange', function () { // Note: We are using statechange instead of popstate
            var state = History.getState(); // Note: We are using History.getState() instead of event.state
            if (that.eventType === 'forward') {
                that.eventType = 'back';
                that.hideContentAsForward(function () {
                    that.handleEvent(state);
                });
            }
            else if (that.eventType === 'back') {
                that.hideContentAsBack(function () {
                    that.handleEvent(state);
                });
            }
        });
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

        handleEvent:function (state) {
            if (state.data.type === 'tree') {
                this.getTree(state.url);
            } else if (state.data.type === 'blob') {
                this.getBlob(state.url);
            } else if (state.data.type === 'blame') {
                this.getBlame(state.url);
            }
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
                        that.pushEvent('tree', pathInfo.url, 'back');
                    }).appendTo($path);
                $path.append($('<span>').addClass('divider').text('/')).appendTo(that.nav);
            }
        },

        pushEvent:function (type, url, eventType) {
            this.eventType = eventType;
            History.pushState({type:type}, null, url);
        },

        replaceEvent:function (type, url) {
            History.replaceState({type:type}, null, url);
        },

        getTree:function (url, pushUrl) {
            pushUrl = pushUrl || url;
            var that = this;

            var cache = that.target.data(pushUrl);
            if (cache) {
                that.renderTree(cache);
                return;
            }

            that.loading.show();
            $.ajax({
                type:"get",
                dataType:"json",
                url:url + "?format=json",
                success:function (data) {
                    that.loading.hide();
                    that.target.data(pushUrl, data);
                    if (pushUrl !== url) {
                        that.target.data(url, data);
                    }
                    that.renderTree(data);
                }
            });
        },

        getBlob:function (url, pushUrl, pushState) {
            pushUrl = pushUrl || url;
            var that = this;

            var cache = that.target.data(pushUrl);
            if (cache) {
                that.renderBlob(cache);
                return;
            }

            that.loading.show();
            $.ajax({
                type:"get",
                dataType:"json",
                url:url + "?format=json",
                success:function (data) {
                    that.loading.hide();
                    that.target.data(pushUrl, data);
                    if (pushUrl !== url) {
                        that.target.data(url, data);
                    }
                    that.renderBlob(data);
                }
            });
        },

        getBlame:function (url, pushUrl) {
            pushUrl = pushUrl || url;
            var that = this;

            var cache = that.target.data(pushUrl);
            if (cache) {
                that.renderBlame(cache);
                return;
            }

            that.loading.show();
            $.ajax({
                type:"get",
                dataType:"json",
                url:url + "?format=json",
                success:function (data) {
                    that.loading.hide();
                    that.target.data(pushUrl, data);
                    if (pushUrl !== url) {
                        that.target.data(url, data);
                    }
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

            var $tr = $('tr', $code);
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
            $('<span>').addClass('icon-txt').appendTo($info);
            $('<span>').text(data.mode).appendTo($info);
            $('<span>').text(data.size + ' kb').appendTo($info);
            var $actions = $('<div>').addClass('actions pull-right').appendTo($info);
            $actions.append($('<a>').attr('href', data.historyUrl).text('history'));
            $actions.append($('<a>').attr('href', data.blameUrl).text('blame'));
            $actions.append($('<a>').attr('href', data.rawUrl).text('raw'));
            return $info;
        },

        renderBlameInfo:function (data) {
            var $info = $('<div>').addClass('blob-info');
            $('<span>').addClass('icon-txt').appendTo($info);
            $('<span>').text(data.raw.mode).appendTo($info);
            $('<span>').text(data.raw.size + ' kb').appendTo($info);
            var $actions = $('<div>').addClass('actions pull-right').appendTo($info);
            $actions.append($('<a>').attr('href', data.historyUrl).text('history'));
            $actions.append($('<a>').attr('href', data.current.url).text('normal view'));
            $actions.append($('<a>').attr('href', data.rawUrl).text('raw'));
            return $info;
        },

        hideContentAsBack:function (callback) {
            var that = this;
            callback = callback || function () {
            };
            this.nextDirection = "left";
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
            var files = data.files;

            // table
            var $table = $('<table>').addClass('tree-browser');

            // thead
            var $thead = $('<thead>').appendTo($table);
            $('<tr>')
                .append($('<th>').text(''))
                .append($('<th>').text('name'))
                .append($('<th>').text('age'))
                .append($('<th>').append($('<div>').append('message')
                .append($('<a>').addClass('history pull-right').attr('href', data.historyUrl).text('history')))).appendTo($thead);

            // tbody
            var $tbody = $('<tbody>').appendTo($table);

            var $tr = $('<tr>').appendTo($tbody);
            this.renderTrAsParent(data.parents, $tr);

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
            direction = direction || this.nextDirection;
            if (!direction || direction === '') {
                this.content.show();
                return;
            }
            this.content.effect('slide', { mode:mode, direction:direction }, 200, callback);
        },

        renderTrAsParent:function (parents, $tr) {
            var that = this;
            if (parents) {
                var parent = parents[parents.length - 1];
                $('<td>').appendTo($tr);
                $('<td>').append($('<a>').text('..').attr('href', parent.url).click(function (e) {
                    e.preventDefault();
                    that.pushEvent('tree', parent.url, 'back');
                })).appendTo($tr);
                $('<td>').appendTo($tr);
                $('<td>').appendTo($tr);
            }
        },

        renderTrAsBlob:function (current, file, $tr) {
            var that = this;
            (function (current, file, $tr) {
                $('<td>').append($('<i>').addClass('icon-txt')).addClass('icon').appendTo($tr);
                $('<td>').addClass('content').append($('<a>').text(file.name).attr('href', file.url).click(function (e) {
                    e.preventDefault();
                    that.pushEvent('blob', file.url, 'forward');
                })).appendTo($tr);
                $('<td>').text(file.commit.date).appendTo($tr);
                var message = $('<td>').append($('<a>').attr('href', file.commit.url).text(file.commit.shortMessage)).appendTo($tr);
                if (file.commit.author.username) {
                    message.append('&nbsp;');
                    message.append('[');
                    message.append($('<a>').addClass('author').attr('href', file.commit.author.url).text(file.commit.author.username));
                    message.append(']');
                }
            })(current, file, $tr);
        },

        renderTrAsTree:function (current, file, $tr) {
            var that = this;
            (function (current, file, $tr) {
                $('<td>').append($('<i>').addClass('icon-dir')).addClass('icon').appendTo($tr);
                $('<td>').addClass('content').append($('<a>').text(file.name).attr('href', file.url).click(function (e) {
                    e.preventDefault();
                    that.pushEvent('tree', file.url, 'forward');
                })).appendTo($tr);
                $('<td>').text(file.commit.date).appendTo($tr);
                var message = $('<td>').append($('<a>').attr('href', file.commit.url).text(file.commit.shortMessage)).appendTo($tr);
                if (file.commit.author.username) {
                    message.append('&nbsp;');
                    message.append('[');
                    message.append($('<a>').addClass('author').attr('href', file.commit.author.url).text(file.commit.author.username));
                    message.append(']');
                }
            })(current, file, $tr);
        }
    });


    $.fn.gitTree = function (url) {
        return this.each(function () {
            var viewer = new TreeViewer($(this));
            viewer.replaceEvent('tree', location.pathname);
            viewer.getTree(url, location.pathname);
        });
    };

    $.fn.gitBlob = function (url) {
        return this.each(function () {
            var viewer = new TreeViewer($(this));
            viewer.replaceEvent('blob', location.pathname);
            viewer.getBlob(url, location.pathname);
        });
    };

    $.fn.gitBlame = function (url) {
        return this.each(function () {
            var viewer = new TreeViewer($(this));
            viewer.getBlame(url, location.pathname);
        });
    };


})(jQuery);