package cn.edu.sicnu.cs.stu.yuxingcai.a444

/**
 * In-memory holder for the player's profile and best score.
 *
 * The assignment only requires that edits need not be persisted to a file,
 * so a process-wide singleton is sufficient to share data between activities.
 */
object PlayerStore {
    var name: String = ""
    var gender: String = ""
    var phone: String = ""
    var highestScore: Int = 0
    var initialized: Boolean = false
}
