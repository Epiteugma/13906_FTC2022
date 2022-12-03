@echo off
title SDK Update
color 2
(
echo **/*
echo !TeamCode/
echo !build.gradle
echo !build.common.gradle
) > .git\info\sparse-checkout
echo ** Saved sparse checkout file
git config core.sparsecheckout true
echo ** Enabled sparse checkout
git remote add ftc https://github.com/FIRST-Tech-Challenge/FtcRobotController
echo ** Added FTC SDK remote
git pull ftc master
echo ** Update complete.
git clean
echo ** Cleanup done.
pause