;
(function ($) {

    var CommitsViewer = function ($this, url, ref, path, offset) {
        this.target = $this;
        this.base = url;
        this.ref = ref;
        this.path = path;
        this.offset = offset;
    }

    $.extend(CommitsViewer.prototype, {

        getCommits: function() {
            var that = this;
            $.ajax({
                type:"get",
                dataType:"json",
                data: {offset: that.offset},
                url: that.createCommitLink(),
                success:function (data) {
                    that.offset++;
                    that.renderCommits(data);
                }
            });
        },

        renderCommits: function(data) {
            var commits = data.commits;
            for (var i = 0; i < commits.length; i++) {
                var commit = commits[i];
                var $commit = $('<div>').addClass('commit');
                $('<time>').text(commit.date).appendTo($commit);

                var $meta = $('<div>').addClass('meta').appendTo($commit);
                $('<p>').addClass('message').text(commit.shortMessage).appendTo($meta);
                $('<br>').appendTo($meta);
                $('<p>').addClass('author').text(commit.author).appendTo($meta);
                $('<p>').addClass('pull-right id').text(commit.id).appendTo($meta);

                this.target.append($commit);
            }
        },

        createCommitLink:function () {
            return this.base + "/commits/" + this.ref + "/" + this.path;
        }

    });

    $.fn.getCommits = function (options) {
        var opts = $.extend({}, $.fn.getCommits.defaults, options);
        return this.each(function () {
            var viewer = new CommitsViewer($(this), opts.url, opts.ref, opts.path, opts.offset);
            viewer.getCommits();
        });
    };

    $.fn.getCommits.defaults = {
        path:"",
        offset: 0
    }

})(jQuery);
