package com.example.colorconquest2

data class tie(
    var player:Int=0, // 0 for no player, 1 for player 1, 2 for player 2
    var color: Int = 0, // 0 for no color, 1 for player 1's color, 2 for player 2's color
    var points: Int = 0 // Initial points
)