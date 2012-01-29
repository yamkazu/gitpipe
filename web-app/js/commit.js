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

        createChanges:function (add, remove) {
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
            $changes.append($('<span>').addClass('add').append(count(add, '+')));
            $changes.append($('<span>').addClass('remove').append(count(remove, '-')));
            $changes.attr('title', add + ' insertions(+), ' + remove + ' deletions(-)');
            $changes.attr('ref', 'twipsy');
            $changes.twipsy();
            return $changes;
        },

        renderCommit:function (data) {
            var diffs = data.diffs;

            var $table = $('<table>').addClass('condensed-table');
            var $tbody = $('<tbody>').appendTo($table);
            var sumAdd = 0;
            var sumRemove = 0;

            for (var i = 0; i < diffs.length; i++) {
                var diff = diffs[i];
                var $tr = $('<tr>');
                if (diff.type === 'ADD') {
                    $('<td>').append($('<span class="label notice">ADD</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + diff.newId).text(diff.newPath)).appendTo($tr);
                } else if (diff.type === 'MODIFY') {
                    $('<td>').append($('<span class="label success">MODIFY</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + diff.newId).text(diff.newPath)).appendTo($tr);
                } else if (diff.type === 'DELETE') {
                    $('<td>').append($('<span class="label important">DELETE</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + diff.oldId).text(diff.oldPath)).appendTo($tr);
                } else if (diff.type === 'RENAME') {
                    $('<td>').append($('<span class="label warning">RENAME</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + diff.newId).text(diff.oldPath + " -> " + diff.newPath)).appendTo($tr);
                } else if (diff.type === 'COPY') {
                    $('<td>').append($('<span class="label">COPY</span>')).appendTo($tr);
                    $('<td>').append($('<a>').attr('href', '#' + diff.newId).text(diff.oldPath + " -> " + diff.newPath)).appendTo($tr);
                }
                $('<td>').append(this.createChanges(diff.add, diff.remove)).appendTo($tr);
                sumAdd += diff.add;
                sumRemove += diff.remove;

                $tr.appendTo($tbody);
            }
            this.target.append($table);
            this.target.append($('<p>').text(diffs.length + ' changed files ' + sumAdd + ' insertions(+), ' + sumRemove + ' deletions(-)'));

            this.target.append($('<h3>').text('Diff'));

            for (var i = 0; i < diffs.length; i++) {
                var diff = diffs[i];
                var $diff = $('<div>').addClass('diff');

                var $title = $('<div>').addClass('title');
                var $path = $('<h4>').appendTo($title);
                if (diff.type === 'ADD') {
                    $path.attr('id', diff.newId);
                    $path.append('<span class="label notice">ADD</span>')
                    $path.append($('<small>').text(diff.newMode));
                    $path.append(diff.newPath);
                    $title.append($('<a>').addClass('pull-right').attr('href', diff.newBlobUrl).text(diff.newId.substr(0, 10)));
                } else if (diff.type === 'MODIFY') {
                    $path.attr('id', diff.newId);
                    $path.append('<span class="label success">MODIFY</span>');
                    $path.append($('<small>').text(diff.newMode));
                    $path.append(diff.newPath);
                    $title.append($('<a>').addClass('pull-right').attr('href', diff.newBlobUrl).text(diff.newId.substr(0, 10)));
                } else if (diff.type === 'DELETE') {
                    $path.attr('id', diff.oldId);
                    $path.append('<span class="label important">DELETE</span>');
                    $path.append($('<small>').text(diff.oldMode));
                    $path.append(diff.oldPath);
                    $title.append($('<a>').addClass('pull-right').attr('href', diff.oldBlobUrl).text(diff.oldId.substr(0, 10)));
                } else if (diff.type === 'RENAME') {
                    $path.attr('id', diff.newId);
                    $path.append('<span class="label warning">RENAME</span>');
                    $path.append($('<small>').text(diff.newMode));
                    $path.append(diff.oldPath + " -> " + diff.newPath);
                    $title.append($('<a>').addClass('pull-right').attr('href', diff.newBlobUrl).text(diff.newId.substr(0, 10)));
                } else if (diff.type === 'COPY') {
                    $path.attr('id', diff.newId);
                    $path.append('<span class="label">COPY</span>');
                    $path.append($('<small>').text(diff.newMode));
                    $path.append(diff.oldPath + " -> " + diff.newPath);
                    $title.append($('<a>').addClass('pull-right').attr('href', diff.newBlobUrl).text(diff.newId.substr(0, 10)));
                }
                $title.appendTo($diff);

                var brush = new SyntaxHighlighter.brushes['Diff']();
                brush.init({toolbar:false, gutter:false});
                var html = brush.getHtml(diff.diff);
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
        }

    });

    $.fn.getCommit = function (url) {
        return this.each(function () {
            var viewer = new CommitViewer($(this));
            viewer.getCommit(url);
        });
    };

})(jQuery);
