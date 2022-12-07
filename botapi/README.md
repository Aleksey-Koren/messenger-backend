# messenger-bot-support

## Documentation
[JavaDoc](https://foltrex.github.io/messenger-bot-support/)

## Source Code
[Pull Request](https://github.com/Foltrex/messenger-bot-support/pull/1)

## Guide
Bots are added by entering their webhook url here, in format like: http://localhost:8081/, where the last slash is neccessary
![Bot webhook url registration modal](https://github.com/Foltrex/messenger-bot-support/blob/gh-pages/register_new_bots.png)
After that they are added to the chat as regular users
![Adding people to chat](https://github.com/Foltrex/messenger-bot-support/blob/gh-pages/add_people_to_the_chat.png)
After that, they will need to impltement the interface in their application, which is in the photo, and launch the application
![Messenger bot interface](https://github.com/Foltrex/messenger-bot-support/blob/gh-pages/messenger_bot_interface.png)
The credential of the bot the user registering it receives after entering the webhook url here:
![Bot webhook url registration modal](https://github.com/Foltrex/messenger-bot-support/blob/gh-pages/register_new_bots.png)
Bot modal after registering webhook url
![Bot registration modal](https://github.com/Foltrex/messenger-bot-support/blob/gh-pages/bot_registration_modal.png)
To determine the functionality, you need to register it in the handleIncomingMessage method, which accepts an object of the next class and returns an object of the same class.
![Message class](https://github.com/Foltrex/messenger-bot-support/blob/gh-pages/message.png)
After that, the message is converted and transmitted back to the messenger application. <br />
Result:
![Result](https://github.com/Foltrex/messenger-bot-support/blob/gh-pages/result.png)
