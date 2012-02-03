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
                $('<div>').addClass('commit-title').append($('<span>').text(commit.shortMessage)).appendTo($commit);

                var $meta = $('<div>').addClass('commit-meta').appendTo($commit);
                if (commit.author.username) {
                    $('<a>').addClass('author').attr('href', commit.author.url).text(commit.author.username).appendTo($meta);
                } else {
                    $('<span>').addClass('author').text(commit.author.name).appendTo($meta);
                }
                $meta.append('&nbsp;');
                $meta.append('authored');
                $('<time>').text(commit.date).appendTo($meta);
                $('<a>').attr('href', commit.url).addClass('pull-right sha-block').append('commit').append('&nbsp;').append($('<span>').addClass('sha').append(commit.id.substr(0, 10))).appendTo($meta);

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
