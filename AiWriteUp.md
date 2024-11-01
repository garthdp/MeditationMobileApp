Garth

For most of the project I did not use AI execept for a few things towards the end of the project. 

First I used it for the one bug I kept encountering and used it to debug that problem debugging.
My input was just the error.
![Screenshot 2024-11-01 132607](https://github.com/user-attachments/assets/5f0446b5-c7ca-4e88-ae3b-2394db3b5bd1)
It suggested to delete my build file.
![Screenshot 2024-11-01 132628](https://github.com/user-attachments/assets/2553629a-85fd-419e-acc2-c3840b4f792a)
After deleteing the build file my problem was resolved. And I then figured out it was because i save the project in OneDrive because i only encountered the problem when OneDrive was running.

The next time i used AI was when trying to implement SSO from a video (https://www.youtube.com/watch?v=zCIfBbm06QM), I did reference the video in the code. I ran into a problem was when he implemented into the main activity because set his main activity differently to what we already had set up because we initially didnt have SSO for part 2. 

First i ask AI to take the GoogleAuthUiClient which i made with help of the video and i told ai to implement it to the main activity so that when the user signs in it will use the functions i got from the video.
First screenshot of the videos code which i couldnt use because out main activity was set up differently.
![Screenshot 2024-11-01 133824](https://github.com/user-attachments/assets/ccde3f75-4705-4ad8-bf7f-73d2e66d6bbb)


Here is me asking chatgpt to make it so that the code made with the video can work with our main activity.
![Screenshot 2024-11-01 133942](https://github.com/user-attachments/assets/9a1a8674-b69f-4f3a-9550-068865f07097)


ChatGPT first told me to add these different functions.
![Screenshot 2024-11-01 134103](https://github.com/user-attachments/assets/c329ca5e-bcc4-42f8-a47a-5451f811b2d3)
![Screenshot 2024-11-01 134122](https://github.com/user-attachments/assets/ead0cf4e-fc1f-4a45-b389-7c6b0bc49f11)


It then told me to call this function in the login button 
![Screenshot 2024-11-01 134111](https://github.com/user-attachments/assets/b7580403-22a9-4fd2-b3c6-5f09b149a266)

It also told me to initial GoogleAuthUiClient in the main activity code
![Screenshot 2024-11-01 134134](https://github.com/user-attachments/assets/08a8e42b-0419-4827-8cc9-f3b608aca02d)

I then had to change one of the functions because this code only signed a user in with firebase auth, it didnt set up the users account which stores their data relating to the app so i added extra code which i had used previously to create a profile for the user to the function.
![Screenshot 2024-11-01 134505](https://github.com/user-attachments/assets/4eb94292-066a-4224-a5c3-99c23ace9064)
![Screenshot 2024-11-01 134530](https://github.com/user-attachments/assets/5e15ebcf-cb70-4e4a-9766-c569abe2a48a)

Firstly it does an api call to our api and checks if the user alreadly exists in the database, if the user doesnt it then runs a post request which adds the user to the database and then returns the user to the next page.
