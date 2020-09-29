package example.testapp.na.screens.main.interactor

import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.NotesRepo
import example.testapp.na.data.preferences.PreferencesHelper
import example.testapp.na.screens.main.dto.ConfigMain
import example.testapp.na.vipercore.interactor.BaseInteractor
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task
import rc.extensions.scope.RCScope
import rc.extensions.scope.singleWorker
import rc.extensions.workers.Workers
import javax.inject.Inject

class MainInteractor @Inject internal constructor(var notestRepo: NotesRepo,
                                                  var prefs: PreferencesHelper): BaseInteractor(), IMainInteractor, RCScope {

    override fun loadAllNotes(onLoaded: () -> Unit): SingleSubscriber<ArrayList<Note>> {
        return notestRepo.getNotes(false)
                .doOnComplete(onLoaded)
    }

    override fun getConfig(onLoaded: () -> Unit): SingleSubscriber<ConfigMain> {
        return singleWorker {
            ConfigMain(prefs.getViewType(), prefs.isFirstLaunch(), prefs.isNightMode())
        }.doOnComplete(onLoaded)
                .completeOn(Workers.ui())
    }

    override fun loadNoteById(id: Long): SingleSubscriber<Note> {
        return notestRepo.getNoteById(id)
    }

    override fun updateNote(note: Note): Task {
        return notestRepo.updateNote(note)
    }

    override fun insertNote(note: Note): Task {
        return notestRepo.insertNote(note)
    }

    override fun deleteNote(id: Long): Task {
        return notestRepo.deleteNote(id)
    }
}