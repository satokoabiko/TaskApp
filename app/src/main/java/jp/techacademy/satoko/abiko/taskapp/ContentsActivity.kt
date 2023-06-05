package jp.techacademy.satoko.abiko.taskapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import jp.techacademy.satoko.abiko.taskapp.databinding.ActivityContentBinding
import java.util.*

class ContentsActivity {
    private lateinit var binding: ActivityContentBinding

    private lateinit var realm: Realm
    private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ボタンのイベントリスナーの設定
        binding.content.search_button.setOnClickListener(dateClickListener)

        // EXTRA_TASKからTaskのcategoryを取得
        val intent = intent
        val category = intent.getIntExtra(EXTRA_TASK, -1)

        // Realmデータベースとの接続を開く
        val config = RealmConfiguration.create(schema = setOf(Task::class))
        realm = Realm.open(config)

        // タスクを取得または初期化
        initTask(category)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Realmデータベースとの接続を閉じる
        realm.close()
    }

    /**
     * 検索ボタン
     */
//    private val doneClickListener = View.OnClickListener {
//        CoroutineScope(Dispatchers.Default).launch {
//            addTask()
//            finish()
//        }
//    }
    val categoryFlow: Task? = realm.query<Task>("_id == $0", PRIMARY_KEY_VALUE).first().find()

    val asyncCall: Deferred<Unit> = async {
        catecoryFlow.collect { results ->
            when (results) {
                // print out initial results
                is InitialResults<Task> -> {
                    for (category in results.list) {
                        Log.v("category: $category")
                    }
                } else -> {
                // do nothing on changes
                }
            }
        }
    }

    /**
     * タスクを取得または初期化
     */
    private fun initTask(taskId: Int) {
        // 引数のtaskIdに合致するタスクを検索
        val findTask = realm.query<Task>("id==$taskId").first().find()

        if (findTask == null) {
            // 新規作成の場合
  //          task = Task()
  //          task.id = -1

        } else {
            // taskの値を画面項目に反映
            binding.content.titleEditText.setText(task.title)
            binding.content.contentEditText.setText(task.contents)
            binding.content.categoryEditText.setText(task.category)
        }
    }
}

class ActivityContentBinding {

}
