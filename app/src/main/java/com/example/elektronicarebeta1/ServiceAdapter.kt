package com.example.elektronicarebeta1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.elektronicarebeta1.models.Service
import java.text.NumberFormat
import java.util.Locale

class ServiceAdapter(private var services: List<Service>) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(service: Service)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_modern, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
    }

    override fun getItemCount(): Int = services.size

    fun updateServices(newServices: List<Service>) {
        services = newServices
        notifyDataSetChanged()
    }

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceIcon: ImageView = itemView.findViewById(R.id.service_icon)
        private val serviceName: TextView = itemView.findViewById(R.id.service_name)
        private val serviceCategory: TextView = itemView.findViewById(R.id.service_category)
        private val serviceDescription: TextView = itemView.findViewById(R.id.service_description)
        private val servicePrice: TextView = itemView.findViewById(R.id.service_price)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(services[position])
                }
            }
        }

        fun bind(service: Service) {
            serviceName.text = service.name
            serviceCategory.text = service.category
            serviceDescription.text = service.description

            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            formatter.maximumFractionDigits = 0
            servicePrice.text = formatter.format(service.basePrice).replace("Rp", "Rp ")

            // Set icon based on category
            when (service.category.lowercase(Locale.getDefault())) {
                "phone" -> serviceIcon.setImageResource(R.drawable.ic_phone_outline)
                "laptop" -> serviceIcon.setImageResource(R.drawable.ic_laptop)
                "tv" -> serviceIcon.setImageResource(R.drawable.ic_tv)
                "printer" -> serviceIcon.setImageResource(R.drawable.ic_printer)
                else -> serviceIcon.setImageResource(R.drawable.ic_settings) // Fallback icon
            }

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Clicked on ${service.name}", Toast.LENGTH_SHORT).show()
                // Future navigation can be handled here by the activity/fragment
                 listener?.onItemClick(service)
            }
        }
    }
}
