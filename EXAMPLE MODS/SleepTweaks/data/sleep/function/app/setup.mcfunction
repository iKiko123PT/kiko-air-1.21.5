#> sleep:app/setup
# Called on load

# Initiate all scoreboard objectives
function sleep:app/scoreboard/add

# Set fixed values for some scoreboards
function sleep:app/scoreboard/set

# Load the config values
function sleep:config/defaults

# Set values for some storages
function sleep:app/storage/set

# Print the image
function sleep:config/image

# Set the playersSleepingPercentage gamerule to 101
# To disable the vanilla sleep system
# Without this the datapack will not work
gamerule playersSleepingPercentage 101