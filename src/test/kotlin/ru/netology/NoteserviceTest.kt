package ru.netology

import org.junit.Test

import org.junit.Assert.*

class NoteserviceTest {
    val serviceNotes = Noteservice()
    val note = Note()
    val addNoteUser = fun() {
        for (j in 1 .. 7) {
            for (i in 0..3) {
                serviceNotes.add(Note(ownerId = j.toUInt(), title = "Title note $i"))
            }
            if (j == 5) serviceNotes.addUser(User(friends = hashSetOf(5u, 6u , 4u, 7u)))
            else serviceNotes.addUser(User(friends = hashSetOf(5u, 6u , 4u)))
        }
    }
    @Test
    fun add() {
        addNoteUser()
        val result = serviceNotes.add(note)
        assertEquals(result, 28)
    }

    @Test
    fun deleteSuccess() {
        val idNote = 2u
        val userId = 5u
        addNoteUser()
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 1)
    }

    @Test
    fun deleteNotfoundNote() {
        val idNote = 32u
        val userId = 5u
        addNoteUser()
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 180)
    }

    @Test
    fun deleteNotfoundUser() {
        val idNote = 12u
        val userId = 7u
        addNoteUser()
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 184)
    }

    @Test
    fun deleteNotNotes() {
        val idNote = 12u
        val userId = 7u
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 185)
    }

    @Test
    fun deleteForOwner1() {
        val idNote = 28u
        val userId = 3u
        val note = Note(privacy = 3, ownerId = 5u)
        addNoteUser()
        serviceNotes.add(note)
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 181)
    }

    @Test
    fun deleteForOwner2() {
        val idNote = 28u
        val userId = 5u
        val note = Note(privacy = 3, ownerId = 5u)
        addNoteUser()
        serviceNotes.add(note)
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 1)
    }

    @Test
    fun deleteForFriend() {
        val idNote = 28u
        val userId = 5u
        val note = Note(privacy = 1, ownerId = 2u)
        addNoteUser()
        serviceNotes.add(note)
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 1)
    }

    @Test
    fun deleteForFriendFriends() {
        val idNote = 28u
        val userId = 7u
        val note = Note(privacy = 2, ownerId = 2u)
        addNoteUser()
        serviceNotes.add(note)
        serviceNotes.addUser(User())
        val result = serviceNotes.delete(idNote, userId)
        assertEquals(result, 1)
    }

    @Test
    fun deleteCommentNotNote() {
        val owner = 2u
        addNoteUser()
        val result = serviceNotes.deleteComment(Note(id = 32u), 0, owner)
        assertEquals(result, 180)
    }
    
    @Test
    fun deleteCommentNotComment() {
        val owner = 2u
        addNoteUser()
        val note = serviceNotes.getById(2u).values.elementAt(0)
        val result = serviceNotes.deleteComment(note, 5, owner)
        assertEquals(result, 186)
    }
    
    @Test
    fun deleteCommentSuccess() {
        val owner = 2u
        addNoteUser()
        val comment = Comment(ownerId = owner)
        val note = serviceNotes.getById(2u).values.elementAt(0)
        serviceNotes.createComment(note, comment)
        val result = serviceNotes.deleteComment(note, 0, owner)
        assertEquals(result, 1)
    }
    
    @Test
    fun deleteCommentAccessOwner() {
        val owner = 2u
        addNoteUser()
        val comment = Comment(ownerId = owner)
        val note = Note(id = owner, privacy = 3)
        serviceNotes.edit(note, owner)
        serviceNotes.createComment(note, comment)
        val result = serviceNotes.deleteComment(note, 0, owner)
        assertEquals(result, 1)
    }

    @Test
    fun deleteCommentAccessDeniedFriend() {
        val owner = 2u
        val friend = 4u
        addNoteUser()
        val comment = Comment(ownerId = owner, commentPrivacy = 3)
        val note = Note(id = owner)
        serviceNotes.edit(note, owner)
        serviceNotes.createComment(note, comment)
        val result = serviceNotes.deleteComment(note, 0, friend)
        assertEquals(result, 183)
    }
    
    @Test
    fun deleteCommentAccessFriends() {
        val owner = 2u
        val friend = 4u
        addNoteUser()
        val comment = Comment(ownerId = owner)
        val note = Note(id = owner, privacy = 1)
        serviceNotes.edit(note, owner)
        serviceNotes.createComment(note, comment)
        val result = serviceNotes.deleteComment(note, 0, friend)
        assertEquals(result, 1)
    }
    
    @Test
    fun deleteCommentAccessDeniedFriendsFriends() {
        val owner = 3u
        val friendFriend = 1u
        addNoteUser()
        serviceNotes.addUser(User())
        val comment = Comment(ownerId = owner, commentPrivacy = 2)
        val note = Note(id = owner)
        serviceNotes.edit(note, owner)
        serviceNotes.createComment(note, comment)
        val result = serviceNotes.deleteComment(note, 0, friendFriend)
        assertEquals(result, 183)
    }

    @Test
    fun deleteCommentAccessFriendsFriends() {
        val owner = 3u
        val friendFriend = 7u
        addNoteUser()
        serviceNotes.addUser(User())
        val comment = Comment(ownerId = owner)
        val note = Note(id = owner, privacy = 2)
        serviceNotes.edit(note, owner)
        serviceNotes.createComment(note, comment)
        val result = serviceNotes.deleteComment(note, 0, friendFriend)
        assertEquals(result, 1)
    }
}