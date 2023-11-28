package com.example.noote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.example.noote.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val budgetListLiveData: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)

            // Ketika tombol tambah diklik
            btnAddNote.setOnClickListener(View.OnClickListener {
                intent.putExtra("EXTRA_NOTE_ID", 0)
                startActivity(intent)
            })

            // Ketika item list diklik
            listView.setOnItemClickListener { adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i) as Note
                if (item.id != "0") {
                    updateId = item.id.toString()
                    intent.putExtra("EXTRA_NOTE_ID", updateId)
                    startActivity(intent)
                }
            }
        }
        observeNote()
        getAllNotes()
    }

    private fun getAllNotes(){
        observeNoteChanges()
    }

    private fun observeNoteChanges() {
        noteCollectionRef.addSnapshotListener{ snapshots, error ->
            if (error != null){
                Log.d("MainActivity",
                    "Error listening for budget changes:", error)
            }
            val budgets = snapshots?.toObjects(Note::class.java)
            if(budgets != null){
                budgetListLiveData.postValue(budgets)
            }
        }
    }

    private fun observeNote(){
        budgetListLiveData.observe(this){
                budgets ->
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, budgets.toMutableList())
            binding.listView.adapter = adapter
        }
    }

//    private fun insert(note: Note){
//
//    }
//
//    private fun update(note: Note){
//
//    }
//
//    public fun delete(note: Note){
//
//    }

    companion object {
        private var updateId = ""
        private val firestore = FirebaseFirestore.getInstance()
        val noteCollectionRef = firestore.collection("notes")

        fun delete(note: Note) {
            if(note.id.toString().isEmpty()){
                Log.d("Main Activity", "error delete item! : note Id is empty")
                return
            }
            noteCollectionRef.document(note.id.toString()).delete().addOnFailureListener{
                Log.d("Main Activity", "error deleting note", it)
            }
        }

        fun insert(note: Note) {
            noteCollectionRef.add(note).addOnSuccessListener {
                    documentReference ->
                val createdNoteId = documentReference.id
                note.id = createdNoteId
                documentReference.set(note).addOnFailureListener{
                    Log.d("Main Activity", "Error updating note id: ", it)
                }
            }.addOnFailureListener{
                Log.d("Main Activity", "Error adding note id: ", it)
            }
        }

        fun update(note: Note) {
            note.id = updateId.toString()
            noteCollectionRef.document(updateId.toString()).set(note).
            addOnFailureListener{
                Log.d("Main Activity", "error updating note", it)
            }
        }
    }

}
