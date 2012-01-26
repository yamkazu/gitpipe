;
(function ($) {

    var CommitViewer = function ($this, url, id) {
        this.target = $this;
        this.base = url;
        this.id = id;
    }

    $.extend(CommitViewer.prototype, {

        getCommit: function() {
            var that = this;
            $.ajax({
                type:"get",
                dataType:"json",
                url: that.createCommitLink(),
                success:function (data) {
                    that.renderCommit(data);
                }
            });
        },

        renderCommit: function(data) {
            var diffs = data.diffs;

            var $table = $('<table>').addClass('condensed-table');
            var $tbody  = $('<tbody>').appendTo($table);

            for (var i = 0; i < diffs.length; i++) {
                var diff = diffs[i];

                var $tr = $('<tr>');
                if (diff.type === 'ADD') {
                    $('<td>').append($('<span class="label notice">ADD</span>')).appendTo($tr);
                    $('<td>').text(diff.newPath).appendTo($tr);
                } else if (diff.type === 'MODIFY') {
                    $('<td>').append($('<span class="label success">MODIFY</span>')).appendTo($tr);
                    $('<td>').text(diff.newPath).appendTo($tr);
                } else if (diff.type === 'DELETE') {
                    $('<td>').append($('<span class="label important">DELETE</span>')).appendTo($tr);
                    $('<td>').text(diff.oldPath).appendTo($tr);
                } else if (diff.type === 'RENAME') {
                    $('<td>').append($('<span class="label warning">RENAME</span>')).appendTo($tr);
                    $('<td>').text(diff.oldPath + " -> " + diff.newPath).appendTo($tr);
                } else if (diff.type === 'COPY') {
                    $('<td>').append($('<span class="label">COPY</span>')).appendTo($tr);
                    $('<td>').text(diff.oldPath + " -> " + diff.newPath).appendTo($tr);
                }
                $tr.appendTo($tbody);
            }
            this.target.append($table);

            for (var i = 0; i < diffs.length; i++) {
                var diff = diffs[i];

                var $diff = $('<div>').addClass('diff');

                var $path = $('<h4>');
                if (diff.type === 'ADD') {
                    $path.append('<span class="label notice">ADD</span>').append(diff.newPath);
                } else if (diff.type === 'MODIFY') {
                    $path.append('<span class="label success">MODIFY</span>').append(diff.newPath);
                } else if (diff.type === 'DELETE') {
                    $path.append('<span class="label important">DELETE</span>').append(diff.oldPath);
                } else if (diff.type === 'RENAME') {
                    $path.append('<span class="label warning">RENAME</span>').append(diff.oldPath + " -> " + diff.newPath);
                } else if (diff.type === 'COPY') {
                    $path.append('<span class="label">COPY</span>').append(diff.oldPath + " -> " + diff.newPath);
                }
                $path.appendTo($diff);

                var brush = new SyntaxHighlighter.brushes['Diff']();
                brush.init({toolbar:false, gutter: false});
                var html = brush.getHtml(diff.diff);
                var $code = $(html);
                $('.code div', $code).removeClass('container');
                $('code.string', $code).each(function() {
                    $(this).parent('div').addClass('add');
                }).removeClass('string');
                $('code.color3', $code).each(function() {
                    $(this).parent('div').addClass('remove');
                }).removeClass('color3');

                $code.appendTo($diff);

                this.target.append($diff);
            }
        },

        createCommitLink:function () {
            return this.base + "/commit/" + this.id;
        }

    });

    $.fn.getCommit = function (options) {
        var opts = $.extend({}, $.fn.getCommit.defaults, options);
        return this.each(function () {
            var viewer = new CommitViewer($(this), opts.url, opts.id);
            viewer.getCommit();
        });
    };

    $.fn.getCommit.defaults = {
    }

})(jQuery);
