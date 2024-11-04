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

First screenshot of the videos code which i couldnt use because our main activity was set up differently. This was not submitted to ai it just shows what i was struggling with.

![Screenshot 2024-11-01 133824](https://github.com/user-attachments/assets/ccde3f75-4705-4ad8-bf7f-73d2e66d6bbb)


Here is me asking chatgpt to make it so that the code made with the video can work with our main activity.

![Screenshot 2024-11-01 133942](https://github.com/user-attachments/assets/9a1a8674-b69f-4f3a-9550-068865f07097)


ChatGPT first told me to add these different functions.

![Screenshot 2024-11-01 134103](https://github.com/user-attachments/assets/c329ca5e-bcc4-42f8-a47a-5451f811b2d3)
![Screenshot 2024-11-01 134122](https://github.com/user-attachments/assets/ead0cf4e-fc1f-4a45-b389-7c6b0bc49f11)


It then told me to call this function in the login button 

![Screenshot 2024-11-01 134111](https://github.com/user-attachments/assets/b7580403-22a9-4fd2-b3c6-5f09b149a266)

It also told me to initialize GoogleAuthUiClient in the main activity code

![Screenshot 2024-11-01 134134](https://github.com/user-attachments/assets/08a8e42b-0419-4827-8cc9-f3b608aca02d)

I then had to change one of the functions because this code only signed a user in with firebase auth, it didnt set up the users account which stores their data relating to the app so i added extra code which i had used previously to create a profile for the user to the function.

![Screenshot 2024-11-01 134505](https://github.com/user-attachments/assets/4eb94292-066a-4224-a5c3-99c23ace9064)
![Screenshot 2024-11-01 134530](https://github.com/user-attachments/assets/5e15ebcf-cb70-4e4a-9766-c569abe2a48a)

Firstly it does an api call to our api and checks if the user alreadly exists in the database, if the user doesnt it then runs a post request which adds the user to the database and then returns the user to the next page.

I didnt use AI anywhere else because i used videos and example code from class, i find videos more reliable and easier to understand what the code is used for and why its being used, all things i used a video for has references in the code.

Colby:

For the final part of the POE we knew we wanted to implement a very simple game feature incorporating a whale that's controlled by the user to help users relax and have fun, but did not exactly know what the concept would be about, i then asked AI to help me with a couple concept ideas, this was the prompt and response: 

![Screenshot (565)](https://github.com/user-attachments/assets/fc5734fb-84e1-4908-a3e2-87b916823f86)
![Screenshot (566)](https://github.com/user-attachments/assets/51a80b24-a9de-4591-98a1-1cf6b42bfebd)
![Screenshot (567)](https://github.com/user-attachments/assets/a64f33cb-d650-4c3b-a0c0-48a76debfeda)

We then chose to navigate/control the whale through touching gestures, where the user taps anywhere on the screen and the whale will navigate to that spot,
We also chose to implement the scoring system, that gives users something to play towards, and try and beat their score,
And for avoiding obstacles we chose to implement a falling net , that the whale must try and dodge, and thought this will incorporate well with the sea theme. 

For the game i also used AI called leonardo.ai to generate me cartoon images to use in the game, the promt was : Render a vibrant, 3D cartoon-style fishing net suspended in mid-air, its mesh pattern intricately woven with thick, curved ropes and wooden floats, facing directly downward as if ready to scoop up an underwater catch, with a subtle gradient of light blue and white hues reflecting off its surface, set against a soft, cream-colored background, with bold, expressive lines and textures that evoke a sense of playfulness and whimsy.The ai then generated several images for me to choose from :

![Screenshot (563)](https://github.com/user-attachments/assets/65c4e69b-d070-472d-82f9-9381ee1b5de5)

i then liked this one the most and ended on picking this:

![Screenshot (564)](https://github.com/user-attachments/assets/27f558ee-1384-46d8-8a92-1215fed01451)

We then found videos to help us code the features, like the scoring , obstacles and movements, and referenced them in our code.



