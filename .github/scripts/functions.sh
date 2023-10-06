#!/bin/bash
# @file functions
# @brief CI and local package release "automation" functions.

#
# @description Parse and return a version value from a "package.json" file
#
# @see [shdoc](https://github.com/reconquest/shdoc)
# @example
#   packageVersion "<PATH>/package.json"
packageVersion() {
    local package_json_file=$1
    local version=""
    while read a b ; do 
        [ "$a" = '"version":' ] && { b="${b%\"*}" ; version="${b#\"}" ; break ; }
    done < $package_json_file
    echo $version
}

# @description Add github actions state and output variables to be handled on .yml workflow files
#
# @see $GITHUB_OUTPUT
# @see [Deprecating save-state and set-output commands](https://github.blog/changelog/2022-10-11-github-actions-deprecating-save-state-and-set-output-commands)
# @see [Github Actions: Output parameter](https://docs.github.com/en/actions/using-workflows/workflow-commands-for-github-actions#setting-an-output-parameter)
githubActionsOutputs() {
    # PS: CURRENT_TAG and COMMIT_MESSAGE are handled here as "global/environment" variables
    CURRENT_TAG=$(git describe --tags $(git rev-list --tags --max-count=1))
    COMMIT_MESSAGE=$(git log -1 --pretty=%B)

    echo "[GITHUB VARIABLES] Commit => $COMMIT_MESSAGE"

    # Use the format {name}={value} instead of ::set-output
    echo "TAG=$CURRENT_TAG" >> $GITHUB_OUTPUT
    echo "COMMIT_MESSAGE=$COMMIT_MESSAGE" >> $GITHUB_OUTPUT
}

# @description Copy all content of the package folder to the ROOT dir
#
# @see [shdoc](https://github.com/reconquest/shdoc)
# @example
#   # From the root folder, with "Packages/<PACKAGE_NAME>"
#   copyPackagesContent
#   # result: Copy "Packages/<PACKAGE_NAME>/*.*" to ROOT/
copyPackagesContent() {
    shopt -s extglob dotglob
    cp -rvf "Packages/$PKG_NAME/." "$PKG_ROOT/"
    rm -rf ./Packages
}

# TODO: Move this common function to another script file in order to reuse (e.g .github/scripts/common.sh)
#
# @description Rename invalid package directories to be "untracked" by game engine, adding a "~" suffix
#              (e.g "Samples" => "Samples~", "Documentation" => "Documentation~")
#
# @see $PKG_ROOT
# @see [shdoc](https://github.com/reconquest/shdoc)
renameInvalidDirs() {
    # $PKG_ROOT environment variable check
    # TODO: Move this environment variable verification to a reusable function
    if [ -z "$PKG_ROOT" ]
    then
        PKG_ROOT=$1
        if [ -z "$PKG_ROOT" ]
        then
            PKG_ROOT="."
        fi
    fi

    echo "[RENAME DIRECTORIES] Package Root: $PKG_ROOT/"

    # Rename UPM special directories with suffix "~"
    if [ -d "$PKG_ROOT/Samples" ] && [ ! -d "$PKG_ROOT/Samples~" ]
    then
        mv "$PKG_ROOT/Samples" "$PKG_ROOT/Samples~"
        rm -f "$PKG_ROOT/Samples.meta"

        echo "[RENAMED] Samples => $PKG_ROOT/Samples~"
    fi
    if [ -d "$PKG_ROOT/Documentation" ] && [ ! -d "$PKG_ROOT/Documentation~" ]
    then
        mv "$PKG_ROOT/Documentation" "$PKG_ROOT/Documentation~"
        rm -f "$PKG_ROOT/Documentation.meta"

        echo "[RENAMED] Documentation => $PKG_ROOT/Documentation~"
    fi
}

# 
# @description Commit with a new version of the package and push the $PKG_BRANCH 
#              new orphan branch (usually "upm" branch)
#
# @see $PKG_BRANCH
# @see renameInvalidDirs()
commitAndPush() {
    # Incrementing LAST_RELEASE_TAG+1.
    # Keep here just to store the history, and if need this to the future/others repositories
    #
    # PS: Keep in mind that not always you would like to increment the git tag version (e.g rewriting with force an existent git tag)
    # [[ "$LAST_RELEASE_TAG" =~ (.*[^0-9])([0-9]+)$ ]] && LAST_RELEASE_TAG="${BASH_REMATCH[1]}$((${BASH_REMATCH[2]} + 1))";
    
    local release_version=$(packageVersion "./package.json")

    echo "[COMMIT AND PUSH] New version: $release_version"

    renameInvalidDirs

    git config --global user.name 'github-bot'
    git config --global user.email 'github-bot@users.noreply.github.com'
    git add .
    git commit --allow-empty -am "$COMMIT_MESSAGE"

    echo $release_version > VERSION.md~
    git add VERSION.md~
    git commit -am "fix: Samples => Samples~ and commit a new version: $release_version"
    git push -f -u origin "$PKG_BRANCH"
}

# TODO: Move this function to another script file (e.g .github/scripts/local.sh)
# 
# @description Copy a list of files and dirs from the ROOT to the package folder
#
# @see $PKG_ROOT
# @see [Exit the Bash Script if a Certain Condition Occurs](https://itslinuxfoss.com/exit-bash-script-if-certain-condition-occurs)
# @see [How to Check if a File or Directory Exists in Bash](https://linuxize.com/post/bash-check-if-file-exists)
copyFilesForPublish() {
    local repository_root=$1

    if [ -z "$repository_root" ]
    then
        echo "[COPY FILES] The parameter \$1 => \$repository_root is required: $repository_root"
        return 1
    else
        if [[ "$repository_root" =~ \.$ ]]
        then
            repository_root="$repository_root/"
        fi
        echo "[COPY FILES] \$repository_root: $repository_root"
    fi

    # $PKG_ROOT environment variable check
    # TODO: Move this environment variable verification to a reusable function
    if [ -z "$PKG_ROOT" ]
    then
        PKG_ROOT=$2
        if [ -z "$PKG_ROOT" ]
        then
            PKG_ROOT="."
        fi
    fi

    local pkg_root_full_path=$(realpath $PKG_ROOT)
    if [[ $repository_root == $pkg_root_full_path ]]
    then
        echo "[COPY FILES] Cannot copy a directory FROM: \$repository_root => '$repository_root' to \$PKG_ROOT => '$pkg_root_full_path', into itself"
        return 1
    fi

    chmod -R 777 "$PKG_ROOT/"

    local files_copy=(README.md README.md.meta LICENSE LICENSE.meta Images Images.meta)
    for file_name in "${files_copy[@]}"
    do
        if [[ -f "$repository_root/$file_name" && ! -f "$PKG_ROOT/$file_name" ]] || [[ -d "$repository_root/$file_name" && ! -d "$PKG_ROOT/$file_name" ]]
        then
            cp -rf "$repository_root/$file_name" "$PKG_ROOT/$file_name"
            echo "[COPY FILES] Copied: $PKG_ROOT/$file_name"
        fi
    done
}

# TODO: Move this function to another script file (e.g .github/scripts/local.sh)
# TODO: Move common functions dependencies to another script file in order to reuse (e.g .github/scripts/common.sh)
# 
# @description Automate all actions required before PUBLISH a package in a remote registry
#
# @arg $1 string A path configured as "$repository_root" local variable to be used as an origin
#                path to copy content into package dir
#
# @see $PKG_ROOT
# @see renameInvalidDirs($PKG_ROOT)
# @see copyFilesForPublish($1)
localBeforePublish() {
    local repository_root=$1

    # $PKG_ROOT environment variable check
    # TODO: Move this environment variable verification to a reusable function
    if [ -z "$PKG_ROOT" ]
    then
        PKG_ROOT=$2
        if [ -z "$PKG_ROOT" ]
        then
            PKG_ROOT="."
        fi
    fi

    renameInvalidDirs $PKG_ROOT
    copyFilesForPublish $repository_root
}

# TODO: Move this function to another script file (e.g .github/scripts/local.sh)
# 
# @description PUBLISH a package in a remote registry. Usually used inside of a npm script
#
# @arg $1 string Overrides the $PKG_ROOT environment variable with a path to a package
#
# @see $PKG_ROOT
localPublish() {
    # $PKG_ROOT environment variable check
    # TODO: Move this environment variable verification to a reusable function
    if [ -z "$PKG_ROOT" ]
    then
        PKG_ROOT=$1
        if [ -z "$PKG_ROOT" ]
        then
            PKG_ROOT="."
        fi
    fi

    cd $PKG_ROOT
    npm publish
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
    elif [ $1 == "copyFilesForPublish" ]
    then
        copyFilesForPublish $2 $3
    elif [ $1 == "renameInvalidDirs" ]
    then
        renameInvalidDirs $2
    elif [ $1 == "localBeforePublish" ]
    then
        localBeforePublish $2
    elif [ $1 == "localPublish" ]
    then
        localPublish $2
    else
        echo "[ERROR] INVALID SCRIPT OPERATION"
        exit 1
    fi
}

run $1 $2 $3