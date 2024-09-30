import java.io.Serializable

data class DiaryAdapter(
    val title: String,
    val content: String,
    val date: String,
    val emojiResId: Int
) : Serializable
