Download 'app-debug.apk' at the root of the project to install the app without needing to build the project yourself.

This app is a simple tool to interact with a self hosted Palworld game server's API. You can fetch information of the current state of the server, and POST a few commands as well. The project uses MVVVM pattern. It also uses a simple "manual" dependency injectionion for the ViewModels as well. There are 3 screens total in this app.

1) Config Screen - The first screen you see will be the Config screen, with two text fields. You will need to enter the IP address and the admin password to continue. Any issue with the verification will display an error message below the submit button. The login information is saved to a DataStore on success, and future app launches will automatically try to verify and skip this screen if successful. If either fields are empty, the submit button is disabled. You can hit enter on the top field to put focus onto the bottom field. Hitting enter on the bottom field submits the information.

      ![ConfigScreen (Small)](https://github.com/user-attachments/assets/3940231e-df4b-4e6a-ab33-2ee55ef6fe8d)

2) Overrview Screen - Once you successfully "sign in", you will transition to the Overview screen. Here you will see the name of the server in the navigation bar. There are 3 main sections on this screen, Server info, Live metrics, and Actions. The live metrics use coroutines (respecting app lifecycle) to update the model states every 5 seconds.
The Actions sections provide 2 endpoint actions to the server. 'Save World' simply sends a POST request with no arguments and displays a Toast message when it hears a response. The 'Announce Message' button displays a popup dialog for the user to input text, which then can be submitted to broadcast a message to all players on the serrver. Then there is a 3rd button to navigate you to another screen to see a detailed list of players

      ![OverviewScreen (Small)](https://github.com/user-attachments/assets/534ea12a-f65b-4281-bb2b-233ae1ed5666)


3) Players Screen - This screen displays a LazyColumn of an array of Player objects fetched from the server. It features pull-to-refresh, and animited card when tapping to see a player's details. On successful loads, I added 50 fake player's to the data so the UI has more to interact with. Real players will be at the top of the list when they are actually logged into the game.

      ![PlayersScreen (Small)](https://github.com/user-attachments/assets/8b85f5fd-fd47-4d3c-84fd-8781d79f770e)


Failed network calls are handled gracefully. The nav bar is aware of what page is currently active and changes which buttons are available. One of those nav buttons changes the theme of the app to use your phone's dynamic colors, and saves your preference between app launches. The theme for this app was created using Google's Material Theme Builder here: https://material-foundation.github.io/material-theme-builder/. The other nav button kicks you back to the Config screen. The app utilizes the NavHostController to switch between screens.

Although you see the Config screen first, the Overview screen is actually the first screen onto the navigation stack. It tries to validate the login information (if any), and if it fails, it immediately navigates you to the Config screen. On navigation stack changes, the nav bar shows/hides a back arrow if there are multiple screens on the stack, but I configured the Config screen to not show it. I also overrode the back button behavior to do nothing as well. You can only get back to the Overview screen by successfully logging in again. Navigating to the Player screen does make the nav back arrow appear.

The app supports landscape as well. It saves the states between configuration changes, so everything stays consistent when rotating. The Overview screen even changes it's layout based on screen orientation.

