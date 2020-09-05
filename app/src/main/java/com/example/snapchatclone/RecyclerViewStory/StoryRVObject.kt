package com.example.snapchatclone.RecyclerViewStory

class StoryRVObject(var email: String, var uid: String, var chatOrStory: String,var name:String) {

    override fun equals(other: Any?): Boolean {
        var same=false
        if(other!=null && other is StoryRVObject) {
            same= (other.uid==this.uid)
        }
        return same
    }

    override fun hashCode(): Int {
        var result=17
        result=31*result + if(this.uid==null) 0 else this.uid.hashCode()

        return result

    }


}