package com.dicoding.stroyapp

import com.dicoding.stroyapp.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val newList = ArrayList<ListStoryItem>()
        for (i in 1..10){
            val story = ListStoryItem(
                photoUrl = "photo_url",
                createdAt = "createdAt $i",
                name = "Story $i",
                description = "Description $i",
                lon = 1.0,
                id = "id $i",
                lat = 2.0
            )
            newList.add(story)
        }
        return newList
    }
}