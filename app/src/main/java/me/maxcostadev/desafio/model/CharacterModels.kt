package me.maxcostadev.desafio.model

import java.io.Serializable

class CharacterModel (val name: String, val description: String, val thumbnail: String, val details: String, val comics: String, val events:String , val links: HashMap<String,String>): Serializable