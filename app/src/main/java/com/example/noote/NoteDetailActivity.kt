package com.example.noote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.noote.databinding.ActivityNoteDetailBinding
import java.util.concurrent.ExecutorService

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailBinding
    private var updateId: String = "0"
    private lateinit var executorService: ExecutorService
    private var noteToDelete: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            if (updateId != "0") {
                // Jika updateId tidak 0, berarti sedang mengedit catatan yang sudah ada
                // Ambil data catatan dari database dan tampilkan di EditText

            }

            btnBack.setOnClickListener {
                finish()
            }
            btnSave.setOnClickListener {
                if (updateId == "0") {
                    MainActivity.insert(
                        Note(
                            title = binding.editTextTitle.text.toString(),
                            description = binding.editTextContent.text.toString()
                        )
                    )
                } else {
                    MainActivity.update(
                        Note(
                            id = updateId.toString(),
                            title = editTextTitle.getText().toString(),
                            description = editTextContent.getText().toString(),
                        )
                    )
                    updateId = "0"
                }
                finish()
            }
            btnDelete.setOnClickListener {
                noteToDelete?.let {
                    MainActivity.delete(it)
                    noteToDelete = null // Reset the noteToDelete after deletion
                }
                finish()
            }

        }
    }


    companion object {
        const val EXTRA_NOTE_ID = 0
    }


}
