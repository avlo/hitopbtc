# ~/.bashrc: executed by bash(1) for non-login shells.
# see /usr/share/doc/bash/examples/startup-files (in the package bash-doc)
# for examples

# If not running interactively, don't do anything
case $- in
    *i*) ;;
      *) return;;
esac

# don't put duplicate lines or lines starting with space in the history.
# See bash(1) for more options
HISTCONTROL=ignoreboth

# append to the history file, don't overwrite it
shopt -s histappend

# for setting history length see HISTSIZE and HISTFILESIZE in bash(1)
HISTSIZE=1000
HISTFILESIZE=2000

# check the window size after each command and, if necessary,
# update the values of LINES and COLUMNS.
shopt -s checkwinsize

# If set, the pattern "**" used in a pathname expansion context will
# match all files and zero or more directories and subdirectories.
#shopt -s globstar

# make less more friendly for non-text input files, see lesspipe(1)
[ -x /usr/bin/lesspipe ] && eval "$(SHELL=/bin/sh lesspipe)"

# set variable identifying the chroot you work in (used in the prompt below)
if [ -z "${debian_chroot:-}" ] && [ -r /etc/debian_chroot ]; then
    debian_chroot=$(cat /etc/debian_chroot)
fi

# set a fancy prompt (non-color, unless we know we "want" color)
case "$TERM" in
    xterm-color|*-256color) color_prompt=yes;;
esac

# uncomment for a colored prompt, if the terminal has the capability; turned
# off by default to not distract the user: the focus in a terminal window
# should be on the output of commands, not on the prompt
#force_color_prompt=yes

if [ -n "$force_color_prompt" ]; then
    if [ -x /usr/bin/tput ] && tput setaf 1 >&/dev/null; then
	# We have color support; assume it's compliant with Ecma-48
	# (ISO/IEC-6429). (Lack of such support is extremely rare, and such
	# a case would tend to support setf rather than setaf.)
	color_prompt=yes
    else
	color_prompt=
    fi
fi

#if [ "$color_prompt" = yes ]; then
#    PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '
#else
#    PS1='${debian_chroot:+($debian_chroot)}\u@\h:\w\$ '
#fi

#RED="\[\033[0;31m\]"
#PURPLE="\[\033[1;35m\]"
#YELLOW="\[\033[0;33m\]"
#GREEN="\[\033[0;32m\]"

parse_git_branch() {
 git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/(\1)/'
}
if [ "$color_prompt" = yes ]; then
 PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[01;33m\]$(parse_git_branch)\[\033[00m\]\$ '
else
 PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[01;33m\]$(parse_git_branch)\[\033[00m\]\$ '
 #PS1='${debian_chroot:+($debian_chroot)}\u@\h:\w$(parse_git_branch)\$ '
fi

unset color_prompt force_color_prompt

# If this is an xterm set the title to user@host:dir
case "$TERM" in
xterm*|rxvt*)
    PS1="\[\e]0;${debian_chroot:+($debian_chroot)}\u@\h: \w\a\]$PS1"
    ;;
*)
    ;;
esac

alias vialias='vi ~/.bash_aliases'

# enable color support of ls and also add handy aliases
if [ -x /usr/bin/dircolors ]; then
    test -r ~/.dircolors && eval "$(dircolors -b ~/.dircolors)" || eval "$(dircolors -b)"
    alias ls='ls --color=auto'
    #alias dir='dir --color=auto'
    #alias vdir='vdir --color=auto'

    alias grep='grep --color=auto'
    alias fgrep='fgrep --color=auto'
    alias egrep='egrep --color=auto'
fi

# colored GCC warnings and errors
#export GCC_COLORS='error=01;31:warning=01;35:note=01;36:caret=01;32:locus=01:quote=01'

# Add an "alert" alias for long running commands.  Use like so:
#   sleep 10; alert
alias alert='notify-send --urgency=low -i "$([ $? = 0 ] && echo terminal || echo error)" "$(history|tail -n1|sed -e '\''s/^\s*[0-9]\+\s*//;s/[;&|]\s*alert$//'\'')"'

# Alias definitions.
# You may want to put all your additions into a separate file like
# ~/.bash_aliases, instead of adding them here directly.
# See /usr/share/doc/bash-doc/examples in the bash-doc package.

if [ -f ~/.bash_aliases ]; then
    . ~/.bash_aliases
fi

# enable programmable completion features (you don't need to enable
# this, if it's already enabled in /etc/bash.bashrc and /etc/profile
# sources /etc/bash.bashrc).
if ! shopt -oq posix; then
  if [ -f /usr/share/bash-completion/bash_completion ]; then
    . /usr/share/bash-completion/bash_completion
  elif [ -f /etc/bash_completion ]; then
    . /etc/bash_completion
  fi
fi

## pyenv configs
export PYENV_ROOT="$HOME/.pyenv"
export PATH="$PYENV_ROOT/bin:$PATH"

if command -v pyenv 1>/dev/null 2>&1; then
  eval "$(pyenv init -)"
fi

# JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
JAVA_8_HOME=/home/nick/apps/java/java-1.8.0-openjdk-1.8.0.151-1.b12.el6_9.x86_64
JAVA_9_HOME=/home/nick/apps/java/jdk-9.0.4
#JAVA_HOME=$JAVA_8_HOME
JAVA_HOME=$JAVA_9_HOME
export JAVA_HOME
PATH=$JAVA_HOME/bin:$PATH
export PATH

M2_HOME=/home/nick/apps/maven/apache-maven-3.6.1
export M2_HOME
PATH=$M2_HOME/bin:$PATH
export PATH

# display debug options export MAVEN_OPTS="-Djavax.net.debug=all -Djavax.net.ssl.trustStore=$JAVA_HOME/jre/lib/security/cacerts"
export MAVEN_OPTS="-Djavax.net.ssl.trustStore=$JAVA_HOME/jre/lib/security/cacerts"
export MAVEN_OPTS=$MAVEN_OPTS" -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

#export GROOVY_HOME=/home/cloudera/groovy/groovy-2.5.1
#PATH=$GROOVY_HOME/bin:$PATH
#export PATH

#export GRADLE_HOME=/home/nick/apps/gradle/gradle-4.4.1
export GRADLE_USER_HOME=/home/nick/.gradle
#PATH=$GRADLE_HOME/bin:$PATH
#export PATH

export PIP_HOME=/home/nick/.local
PATH=$PIP_HOME/bin:$PATH
export PATH

export AWS_DEFAULT_PROFILE=nick

SSH_ENV=$HOME/.ssh/environment

#function testclass {
#  mvn clean
#  command="mvn test -Dtest=$1"
#  printf "\n$command\n"
#  $command
#}
#

#
#RED="\[\033[0;31m\]"
#YELLOW="\[\033[0;33m\]"
#GREEN="\[\033[0;32m\]"
#NO_COLOR="\[\033[0m\]"
#
##PROMPT="cmd"
#
##PS1="$GREEN\u@\h$NO_COLOR:\w$YELLOW\$(parse_git_branch)$NO_COLOR\$ "
#PS1="$GREEN$NO_COLOR\w$YELLOW\$(parse_git_branch)$NO_COLOR\$ "
#

# start the ssh-agent
function start_agent {
  echo "Initializing new SSH agent..."
  # spawn ssh-agent
  /usr/bin/ssh-agent | sed 's/^echo/#echo/' > "${SSH_ENV}"
  echo succeeded
  chmod 600 "${SSH_ENV}"
  . "${SSH_ENV}" > /dev/null
  /usr/bin/ssh-add
}

if [ -f "${SSH_ENV}" ]; then
  . "${SSH_ENV}" > /dev/null
  ps -ef | grep ${SSH_AGENT_PID} | grep ssh-agent > /dev/null || {
    start_agent;
  }
else
    start_agent;
fi

#function __ps1_newline_login {
#  if [[ -z "${PS1_NEWLINE_LOGIN}" ]]; then
#    PS1_NEWLINE_LOGIN=true
#  else
#    printf '\n'
#  fi
#}
#
##PROMPT_COMMAND='__ps1_newline_login'
##export PS1="$ "
## .bashrc

## User specific aliases and functions
#
## Source global definitions
#if [ -f /etc/bashrc ]; then
#	. /etc/bashrc
#fi

function fbk {
if [ -f "$1~" ]; then
	read -n1 -p "$1~ already exists, replace? [y,n]" doit 
  case $doit in  
    y|Y) 
      cp "$1" "$1~" ;
      printf "\n$1~ created\n" ;;
    n|N) 
      printf "\n" ;;
    *) echo dont know ;; 
  esac
  return 0
fi

cp "$1" "$1~" 
printf "\n$1~ created\n"
}

#THIS MUST BE AT THE END OF THE FILE FOR SDKMAN TO WORK!!!
export SDKMAN_DIR="/home/nick/.sdkman"
[[ -s "/home/nick/.sdkman/bin/sdkman-init.sh" ]] && source "/home/nick/.sdkman/bin/sdkman-init.sh"
