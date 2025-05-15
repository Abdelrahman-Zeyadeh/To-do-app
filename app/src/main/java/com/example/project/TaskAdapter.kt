import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Task
import com.example.project.R

class TaskAdapter(private val tasks: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.taskText)  // <-- هنا عدلنا id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)  // <-- عدلنا Layout هنا
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskText.text = "${task.title} - ${task.priority}"

        // تحديد لون النص حسب الأولوية
        when (task.priority.lowercase()) {
            "high" -> holder.taskText.setTextColor(Color.RED)
            "medium" -> holder.taskText.setTextColor(Color.parseColor("#FFA500")) // برتقالي
            "low" -> holder.taskText.setTextColor(Color.GREEN)
            else -> holder.taskText.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int = tasks.size
}
