#!/bin/bash

# USAGE: packageVersion "[PATH]/package.json"
packageVersion() {
    local PACKAGE_JSON_FILE=$1
    VERSION=""
    while read a b ; do 
        [ "$a" = '"version":' ] && { b="${b%\"*}" ; VERSION="${b#\"}" ; break ; }
    done < $PACKAGE_JSON_FILE
    echo $VERSION
}

# @description Add github actions state and output variables to be handled on .yml workflow files
#
# @see [shdoc](https://github.com/reconquest/shdoc)
# @see [Deprecating save-state and set-output commands](https://github.blog/changelog/2022-10-11-github-actions-deprecating-save-state-and-set-output-commands)
githubActionsOutputs() {
    CURRENT_TAG=$(git describe --tags $(git rev-list --tags --max-count=1))
    COMMIT_MESSAGE=$(git log -1 --pretty=%B)
    # Use the format {name}={value} instead of ::set-output
    echo "{tag}={$CURRENT_TAG}" >> $GITHUB_OUTPUT
    echo "{commit_message}={$COMMIT_MESSAGE}" >> $GITHUB_OUTPUT
}

copyPackagesContent() {
    shopt -s extglob dotglob
    cp -rvf "Packages/$PKG_NAME/." "$PKG_ROOT"
    rm -rf ./Packages
}

commitAndPush() {
    # Incrementing LAST_RELEASE_TAG+1.
    # Keep here just to store the history, and if need this to the future/others repositories
    #
    # PS: Keep in mind that not always you would like to increment the git tag version (e.g rewriting with force an existent git tag)
    # [[ "$LAST_RELEASE_TAG" =~ (.*[^0-9])([0-9]+)$ ]] && LAST_RELEASE_TAG="${BASH_REMATCH[1]}$((${BASH_REMATCH[2]} + 1))";
    
    RELEASE_VERSION=$(packageVersion "./package.json")

    echo "New version: $RELEASE_VERSION"

    if [[ -d "Samples" ]]; then
    mv Samples Samples~
    rm -f Samples.meta
    fi
    if [[ -d "Documentation" ]]; then
    mv Documentation Documentation~
    rm -f Documentation.meta
    fi
    git config --global user.name 'github-bot'
    git config --global user.email 'github-bot@users.noreply.github.com'
    git add .
    git commit --allow-empty -am "$COMMIT_MESSAGE"

    echo $RELEASE_VERSION > VERSION.md~
    git add VERSION.md~
    git commit -am "fix: Samples => Samples~ and commit a new version: $RELEASE_VERSION"
    git push -f -u origin "$PKG_BRANCH"
}

run() {
    if [ $1 == "push" ]
    then
        commitAndPush
    elif [ $1 == "movePackagesFolder" ]
    then
        copyPackagesContent
    elif [ $1 == "githubActionsVariables" ]
    then
        githubActionsOutputs
    else
        echo "[ERROR] INVALID SCRIPT OPERATION"
        exit 1
    fi
}

run $1