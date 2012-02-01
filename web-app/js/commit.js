;
(function ($) {

    var CommitViewer = function ($this) {
        this.target = $this;
    }

    $.extend(CommitViewer.prototype, {

        getCommit:function (url) {
            var that = this;
            $.ajax({
                type:"get",
                dataType:"json",
                url:url,
                success:function (data) {
                    that.renderCommit(data);
                }
            });
        },

        createChanges:function (addLine, removeLine) {
            function count(number, ch) {
                var result = number / 5;
                result = result > 10 ? 10 : result;
                var chars = "";
                for (var i = 0; i < result; i++) {
                    chars = chars + ch;
                }
                return chars;
            }

            var $changes = $('<div>').addClass('changes');
            $changes.append($('<span>').addClass('add').append(count(addLine, '+')));
            $changes.append($('<span>').addClass('remove').append(count(removeLine, '-')));
            $changes.attr('title', addLine + ' insertions(+), ' + removeLine + ' deletions(-)');
            $changes.attr('ref', 'twipsy');
            $changes.twipsy();
            return $changes;
        },

        renderCommitInFiles: function(entries) {
            var $table = $('<table>').addClass('condensed-table');
            var $tbody = $('<tbody>').appendTo($table);
            var sumAdd = 0;
            var sumRemove = 0;

            for (var i = 0; i < entries.length; i++) {
                var entry = entries[i];
                var $tr = $('<tr>');
                if (entry.type === 'ADD') {
                    $('<td>').append($('<span class="label notice">ADD</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + entry.newFile.id).text(entry.newFile.path)).appendTo($tr);
                } else if (entry.type === 'MODIFY') {
                    $('<td>').append($('<span class="label success">MODIFY</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + entry.newFile.id).text(entry.newFile.path)).appendTo($tr);
                } else if (entry.type === 'DELETE') {
                    $('<td>').append($('<span class="label important">DELETE</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + entry.oldFile.id).text(entry.oldFile.path)).appendTo($tr);
                } else if (entry.type === 'RENAME') {
                    $('<td>').append($('<span class="label warning">RENAME</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + entry.newFile.id).text(entry.oldFile.path + " -> " + entry.newFile.path)).appendTo($tr);
                } else if (entry.type === 'COPY') {
                    $('<td>').append($('<span class="label">COPY</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + entry.newFile.id).text(entry.oldFile.path + " -> " + entry.newFile.path)).appendTo($tr);
                }
                $('<td>').append(this.createChanges(entry.addLine, entry.removeLine)).appendTo($tr);
                sumAdd += entry.addLine;
                sumRemove += entry.removeLine;

                $tr.appendTo($tbody);
            }
            this.target.append($table);
            this.target.append($('<p>').text(entries.length + ' changed files ' + sumAdd + ' insertions(+), ' + sumRemove + ' deletions(-)'));
        },

        renderDiffs:function(entries) {
            this.target.append($('<h3>').text('Diffs'));
            for (var i = 0; i < entries.length; i++) {
                var entry = entries[i];
                var $diff = $('<div>').addClass('diff');

                var $title = $('<div>').addClass('title');
                var $path = $('<h4>').appendTo($title);
                if (entry.type === 'ADD') {
                    $path.attr('id', entry.newFile.id);
                    $path.append('<span class="label notice">ADD</span>')
                    $path.append($('<small>').text(entry.newFile.mode));
                    $path.append(entry.newFile.path);
                    $title.append($('<a>').addClass('pull-right').attr('href', entry.newFile.blobUrl).text(entry.newFile.id.substr(0, 10)));
                } else if (entry.type === 'MODIFY') {
                    $path.attr('id', entry.newFile.id);
                    $path.append('<span class="label success">MODIFY</span>');
                    $path.append($('<small>').text(entry.newFile.mode));
                    $path.append(entry.newFile.path);
                    $title.append($('<a>').addClass('pull-right').attr('href', entry.newFile.blobUrl).text(entry.newFile.id.substr(0, 10)));
                } else if (entry.type === 'DELETE') {
                    $path.attr('id', entry.oldFile.id);
                    $path.append('<span class="label important">DELETE</span>');
                    $path.append($('<small>').text(entry.oldFile.mode));
                    $path.append(entry.oldFile.path);
                    $title.append($('<a>').addClass('pull-right').attr('href', entry.oldFile.blobUrl).text(entry.oldFile.id.substr(0, 10)));
                } else if (entry.type === 'RENAME') {
                    $path.attr('id', entry.newFile.id);
                    $path.append('<span class="label warning">RENAME</span>');
                    $path.append($('<small>').text(entry.newFile.mode));
                    $path.append(entry.oldFile.path + " -> " + entry.newFile.path);
                    $title.append($('<a>').addClass('pull-right').attr('href', entry.newFile.blobUrl).text(entry.newFile.id.substr(0, 10)));
                } else if (entry.type === 'COPY') {
                    $path.attr('id', entry.newFile.id);
                    $path.append('<span class="label">COPY</span>');
                    $path.append($('<small>').text(entry.newFile.mode));
                    $path.append(entry.oldFile.path + " -> " + entry.newFile.path);
                    $title.append($('<a>').addClass('pull-right').attr('href', entry.newFile.blobUrl).text(entry.newFile.id.substr(0, 10)));
                }
                $title.appendTo($diff);

                var brush = new SyntaxHighlighter.brushes['Diff']();
                brush.init({toolbar:false, gutter:false});
                var html = brush.getHtml(entry.diff);
                var $code = $(html);
                $('.code div', $code).removeClass('container');
                $('code.string', $code).each(
                    function () {
                        $(this).parent('div').addClass('add');
                    }).removeClass('string');
                $('code.color3', $code).each(
                    function () {
                        $(this).parent('div').addClass('remove');
                    }).removeClass('color3');

                $code.appendTo($diff);
                this.target.append($diff);
            }

        },

        renderCommit:function (data) {
            var entries = data.entries;
            this.renderCommitInFiles(entries);
            this.renderDiffs(entries);
        }

    });

    $.fn.getCommit = function (url) {
        return this.each(function () {
            var viewer = new CommitViewer($(this));
            viewer.getCommit(url);
        });
    };

})(jQuery);
