;
(function ($) {

    var CommitsViewer = function ($this) {
        this.target = $this;
        var that = this;
        this.readButton = $('<a>').addClass('btn small read-more').text('read more').click(function(e){
            e.preventDefault();
            that.getCommits($(this).attr('href'));
        }).hide();
        this.target.after(this.readButton);
        this.loading = $('<div>').addClass('loading').show();
        this.target.after(this.loading);
    }

    $.extend(CommitsViewer.prototype, {
        getCommits:function (url) {

            if(url.match(/\?/)) {
                url += '&';
            } else {
                url += '?';
            }
            url += 'format=json';

            var that = this;
            that.loading.show();
            that.readButton.hide();
            $.ajax({
                type:"get",
                dataType:"json",
                url: url,
                success:function (data) {
                    that.loading.hide();
                    that.readButton.show();
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
