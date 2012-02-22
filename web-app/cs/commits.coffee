$ = jQuery

$.fn.extend
  getCommits: (url) ->
    return @each () ->
      new CommitsViewer($(this)).getCommits(url)

class CommitsViewer

  constructor: (@target) ->
    @readButton = $('<a>').addClass('btn small read-more').text('read more').hide().on "click", (e) =>
      e.preventDefault()
      @getCommits(@readButton.attr('href'))
    @target.after(@readButton)

    @loading = $('<div>').addClass('loading').show()
    @target.after(@loading)

  getCommits: (url) ->
    @loading.show()
    @readButton.hide()
    $.getJSON url + (if url.match(/\?/) then '&' else '?') + 'format=json', (data) =>
      @loading.hide()
      @readButton.show()
      @renderCommits(data)

  renderCommits: (data) ->
    for commit in data.commits
      @renderCommit(commit)

    if data.next then @readButton.attr 'href', data.next else @readButton.remove()

  renderCommit: (commit) ->
    $commit = $('<div>').addClass('commit')
    $('<div>').addClass('commit-title').append($('<span>').text(commit.shortMessage)).appendTo($commit)

    $meta = $('<div>').addClass('commit-meta').appendTo($commit)
    if commit.author.username
      $('<a>').addClass('author').attr('href', commit.author.url).text(commit.author.username).appendTo($meta)
    else
      $('<span>').addClass('author').text(commit.author.name).appendTo($meta)

    $meta.append('&nbsp;authored')
    $('<time>').text(commit.date).appendTo($meta);
    $('<a>').attr('href', commit.url).addClass('pull-right sha-block')
      .append('commit&nbsp;')
      .append(
        $('<span>').addClass('sha').append(commit.id[0...10])
      ).appendTo($meta);
    @target.append($commit);
