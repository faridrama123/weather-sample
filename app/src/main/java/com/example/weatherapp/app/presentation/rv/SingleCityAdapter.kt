import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.SingleCity
import com.example.weatherapp.R


class CityAdapter(private val cities: List<SingleCity>, private val clickListener: (SingleCity) -> Unit) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityNameTextView: TextView = itemView.findViewById(R.id.city_name)

        init {
            itemView.setOnClickListener {
                clickListener(cities[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]
        holder.cityNameTextView.text = city.name
    }

    override fun getItemCount() = cities.size
}
