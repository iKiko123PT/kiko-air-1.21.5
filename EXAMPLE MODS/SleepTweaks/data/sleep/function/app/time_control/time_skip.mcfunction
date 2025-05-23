#> sleep:app/time_control/time_skip

$execute as @a[scores={sleep.time_in_bed=$(time_until_skip_ticks)}] run time add $(skip_time)

# weather clear
$execute if predicate sleep:weather_check_thunder run execute as @a[scores={sleep.time_in_bed=$(time_until_skip_ticks)..}] run weather clear

$execute if predicate sleep:weather_check_raining run execute as @a[scores={sleep.time_in_bed=$(time_until_skip_ticks)..}] run weather clear
