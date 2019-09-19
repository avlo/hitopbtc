function loadComments () {

  this.source = null;

  this.start = function () {
    var commentTable = document.getElementById("comments");
    this.source = new EventSource("/comment/stream");
    this.source.addEventListener("message", function (event) {

      var comment = JSON.parse(event.data);
      comments.innerHTML = comment;
    });

    this.source.onerror = function () {
      this.close();
    };
  };

  this.stop = function() {
    this.source.close();
  }
}

comment = new loadComments();

/*
 * Register callbacks for starting and stopping the SSE controller.
 */
window.onload = function() {
  comment.start();
};

window.onbeforeunload = function() {
  comment.stop();
}