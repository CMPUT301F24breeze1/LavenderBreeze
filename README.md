
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



