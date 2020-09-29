package example.testapp.na.screens.edit.interactor

import android.os.Bundle
import example.testapp.na.vipercore.interactor.BaseInteractor
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.NotesRepo
import example.testapp.na.data.preferences.PreferencesHelper
import example.testapp.na.screens.edit.dto.ConfigEdit
import example.testapp.na.tools.credentials.Extras
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task
import rc.extensions.scope.*
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject

class EditInteractor @Inject internal constructor(var prefsHelper: PreferencesHelper, var notesRepo: NotesRepo) :
        BaseInteractor(), IEditInteractor, RCScope {

    private var extras: Bundle? = null

    override fun loadExtras(extras: Bundle?) {
        this.extras = extras
    }

    override fun loadConfig(onLoaded: () -> Unit): SingleSubscriber<ConfigEdit> {
        return singleWorker {
            ConfigEdit(prefsHelper.isNightMode(),
                    extras?.getBoolean(Extras.EDIT_MODE, false) ?: false,
                    extras?.getLong(Extras.EXTRA_NOTE_ID, 0L) ?: 0L,
                    extras?.getInt(Extras.EXTRA_NOTE_PARAM, -1) == Extras.NOTE_PARAM_QUICK,
                    extras?.getInt(Extras.EXTRA_NOTE_PARAM, -1) == Extras.NOTE_PARAM_LIST,
                    extras?.getInt(Extras.EXTRA_NOTE_PARAM, -1) == Extras.NOTE_PARAM_ALARM,
                    extras?.getInt(Extras.EXTRA_NOTE_PARAM, -1) == Extras.NOTE_PARAM_AUDIO)
        }.doOnComplete(onLoaded)
    }

    override fun loadNote(noteId: Long): SingleSubscriber<Note> {
        return notesRepo.getNoteById(noteId)
    }

    override fun addNote(note: Note, onSaved: () -> Unit): Task {
        return notesRepo.insertNote(note)
                .completeOn(Workers.ui())
                .doOnComplete(onSaved)
    }

    override fun updateNote(note: Note, onSaved: () -> Unit): Task {
        return notesRepo.updateNote(note)
                .completeOn(Workers.ui())
                .doOnComplete(onSaved)
    }

    override fun createNoteId(subscription: Subscription<Long>): SingleSubscriber<Long> {
        return notesRepo.createNoteId()
                .subscribe(subscription, Workers.default())
    }
}