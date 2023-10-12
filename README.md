# QR Code Quiz Game 
- A team project I participated in creating at University of Pannonia. 
- It is about teams of people reading QR codes around university campus via an **Android app**, which bring up trivia quiz questions regarding the University and Computer Science in general. Teams compete to answer the questions as fast as possible, and whoever answers all questions first, wins! The current location and progress of teams can be tracked on the **web app**. Team locations update on the website's map dynamically as data in the realtime database changes.
- The project is built using java for the **android app** and html/css/ and vanilla javascript for the **web app**. Both parts of the system uses Firebase Realtime Database to read and write data of the competing teams.
- Credit to the team who worked on this project: Benedixtus, BenPenzes, krisztofer0831, and sparkjet
- The android project folder is `/QRGameAndroid`. The project can be built using Android Studio. (with a version that came out in 2022 or later)
- The web project folder is `/QRGameWeb`. The web app can be immediately launched by opening `/QRCodeGame/QRGameWeb/public/index.html`.
- The game can be played by compiling and lunching the android app. To participate and answer questions the QR codes of the questions have to be read! The QR codes of the 4 base questions can be found in the project folder: `/QRCodes/tasks` (**note**: there is a single QR code of the default team in the `/QRCodes/teams` folder, which we can join by reading its QR code via the Android app!)
- Also, the project can be replicated by creating a custom Firebase project with the proper database structure! For this, a Firebase Realtime Database has to be instantiated and 2 apps needs to be assigned to the Firebase project: an android app and a web app. [Here is the documentation for getting started with Firebase](https://firebase.google.com/docs/guides). In order to connect to the newly created database, the [Firebase configuration objects](https://firebase.google.com/docs/web/setup) have to be modified in both applications (read the docs for more details). If everything is set up, including the connection to the database, the only thing left to do is to set up the database structure and the security rules. Both the database and security rules follow a `.json` format.
    - The database structure can be imported by clicking on the *Import JSON* button in the Firebase console of the realtime database. The path to the `.json` file in the project folder is `/DatabaseInfo/db_structure.json`
    - The security rules can also be set in the Firebase console in the Rules tab! Here, the rules can just be copy-pasted and published with one click. The path to the `.json` file of the security rules is: `/DatabaseInfo/security_rules.json`