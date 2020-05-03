function registerSSEvent() {

  this.source = null;

  this.start = function () {
    var result = document.getElementById("result");
    this.source = new EventSource("/receipt-sse");
    this.source.addEventListener("message", function (event) {

    // These events are JSON, so parsing and DOM fiddling are needed
    var parsedJson = JSON.parse(event.data);
    result.innerHTML = parsedJson.name;

    this.source.onerror = function () {
      this.close();
    };
  };

  this.stop = function() {
    this.source.close();
  }
}

comment = new registerSSEvent();

/*
 * Register callbacks for starting and stopping the SSE controller.
 */
window.onload = function() {
  parsedJson.start();
};

window.onbeforeunload = function() {
  parsedJson.stop();
}