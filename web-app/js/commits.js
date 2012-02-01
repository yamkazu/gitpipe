;
(function ($) {

    var CommitsViewer = function ($this) {
        this.target = $this;
        var that = this;
        this.readButton = $('<a>').addClass('btn small').text('more read').click(function(e){
            e.preventDefault();
            that.getCommits($(this).attr('href'));
        });
        this.target.after(this.readButton);
    }

    $.extend(CommitsViewer.prototype, {
        getCommits:function (url) {
            var that = this;
            $.ajax({
                type:"get",
                dataType:"json",
                url: url,
                success:function (data) {
                    that.renderCommits(data);
                }
            });
        },

        renderCommits:function (data) {
            var commits = data.commits;
            for (var i = 0; i < commits.length; i++) {
                var commit = commits[i];
                var $commit = $('<div>').addClass('commit');
                $('<time>').text(commit.date).appendTo($commit);

                var $meta = $('<div>').addClass('meta').appendTo($commit);
                $('<p>').addClass('message').text(commit.shortMessage).appendTo($meta);
                $('<br>').appendTo($meta);

                if (commit.author.username) {
                    $('<p>').addClass('author').append($('<a>').attr('href', commit.author.url).text(commit.author.username)).appendTo($meta);
                } else {
                    $('<p>').addClass('author').text(commit.author.name).appendTo($meta);
                }
                $('<p>').addClass('pull-right id').append($('<a>').attr('href', commit.url).text(commit.id.substr(0, 10))).appendTo($meta);

                this.target.append($commit);
            }

            if (!data.next) {
                this.readButton.remove();
            } else {
                this.readButton.attr('href', data.next);
            }
        }

    });

    $.fn.getCommits = function (url) {
        return this.each(function () {
            var viewer = new CommitsViewer($(this));
            viewer.getCommits(url);
        });
    };


})(jQuery);
