function registerSSEvent() {
  this.start = function () {
    this.source = new EventSource("/receipt-sse");
    this.source.addEventListener("message", function (event) {
      $(".container-contact100-form-btn").hide();
      $(".contact50-form-title").hide();
      document.getElementById("name").innerHTML = JSON.parse(event.data).name;
    });
    
    this.source.onerror = function () {
      this.close();
    };
  };

  this.stop = function() {
    this.source.close();
  }
}

registerSSEvent = new registerSSEvent();

window.onload = function() {
  registerSSEvent.start();
}

window.onbeforeunload = function() {
  registerSSEvent.stop();
}