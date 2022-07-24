# Stock Market Simulator (Web)

This is stock simulator created using Spring Boot. It emulates stock market allowing users add/sell stock.

### API

- Manipulation of orders is done via REST protocol. Look at [Swagger](http://localhost:8080/sms/swagger-ui)
- All events are sent to WebSocket topic as eht happen. Look at [sample page](http://localhost:8080/sms) to see events.
- To run in CLI mode use "cli" argument.

The app is stateless and holds all data in RAM.