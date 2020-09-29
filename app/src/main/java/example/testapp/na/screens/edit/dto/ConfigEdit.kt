package example.testapp.na.screens.edit.dto

data class ConfigEdit(
        var isNightMode: Boolean,
        var editMode: Boolean,
        var noteId: Long,
        var withEmptyNote: Boolean,
        var withEmptyList: Boolean,
        var withAlarmConfig: Boolean,
        var withAudioRecord: Boolean
)