
# LavenderBreeze Git Workflow

### Cloning the Repository
If you don't have anything on your computer, clone the repository:
```bash
git clone https://github.com/CMPUT301F24breeze1/LavenderBreeze.git
```

### Adding Features

If you're coming back to add new features, follow these steps:

1. **Pull the Latest Changes**
   - Pull the changes from the main branch (this will get everything that got changed):
   ```bash
   git pull origin main
   ```

2. **Create Your Own Branch**
   - Create a new branch to work on your own feature:
   ```bash
   git checkout -b <branch_name>
   ```
   - `checkout` is just switching branches.

3. **Add Files to Track**
   - Add all the files to stage for commit:
   ```bash
   git add .
   ```

4. **Commit Your Changes**
   - Commit your changes with a message:
   ```bash
   git commit -m "your_commit_message"
   ```

5. **Push to Your Own Branch**
   - Push the changes to your branch:
   ```bash
   git push origin <branch_name>
   ```

6. **Create a Pull Request**
   - Go to the GitHub repository.
   - Click on "Pull Request."
   - Click on compare and merge

### Deleting the Branch

Once your pull request is merged, you can delete your branch:

- **Delete Local Branch**:
  ```bash
  git checkout main
  git branch -d <branch_name>
  ```

- **Delete Remote Branch**:
  ```bash
  git push origin --delete <branch_name>
  ```


### If you are confused about something

- **What branch you are on**:
```bash
git status
```
### Team Members
| CCID | GitHub Username |
| -------- | ------- |
| kdcarlso | kenesteria |
| cyrus1 | cyrusgarg |
| tmd | teresaduong |
| pzh | peterzdhuang |
| shammir | ShammirJ |
| louellet | VahArachne |

### Overview

Lavender Breeze is an easy to use app which can be used to plan, join, and run local events smoothly. The app includes the ability to create a user
profile through which you can join events, or an organizer profile where you can save your facility profile and organize events which are easily joinable
through an automatically generated QR code. The app allows organizers to give everyone a fair chance to be a part of the event, with a lottery draw system
determining which entrants on the waitlist get to enroll.

### Features

- Creation/Updation/Deletion of events
- Joining/Leaving events through QR code
- Personalize your user profile with a custom or deterministic profile picture
- See where entrants join your event's waitlist from with geolocation
- Create/Manage your facility profile
- Lottery to select entrants from the waitlist for exclusive events
- Automatic drawing of a new entrant if an invitee takes too long to respond

### Wiki Page

For more detailed information about the app, please refer to our wiki page linked [here]([url](https://github.com/CMPUT301F24breeze1/LavenderBreeze/wiki)):
