package gppmds.wikilegis.view;

import android.app.SearchManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import gppmds.wikilegis.R;
import gppmds.wikilegis.controller.BillController;
import gppmds.wikilegis.controller.DataDownloadController;
import gppmds.wikilegis.controller.LoginController;

import gppmds.wikilegis.exception.BillException;
import gppmds.wikilegis.exception.SegmentException;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabs = null;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BillController billController = BillController.getInstance(getApplicationContext());
        DataDownloadController dataCenter = DataDownloadController.getInstance(getBaseContext());

        if(dataCenter.connectionType() < 2) {
            try {
                billController.getAllBillsFromApi();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (BillException e) {
                e.printStackTrace();
            }
        } else {
            try {
                billController.downloadBills();
            } catch (BillException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SegmentException e) {
                e.printStackTrace();
            }
        }
        settingView();
    }

    private void settingView() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        floatingActionButton = (FloatingActionButton)findViewById
                (R.id.floatingButton);
        floatingActionButton.setVisibility(View.INVISIBLE);

        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(tabsAdapter);

        tabs = (TabLayout) this.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        SharedPreferences session = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext());

        boolean isLogged = session.getBoolean("IsLoggedIn", false);

        if (isLogged) {
            getMenuInflater().inflate(R.menu.menu_logged, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_deslogged, menu);
        }

        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager =
                (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setQueryHint("Pesquisar projetos...");

        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("searchQuery", query);

                SearchBillFragment searchBillFragment = new SearchBillFragment();
                searchBillFragment.setArguments(bundle);

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    fm.popBackStack();
                }

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        fm.beginTransaction();

                fragmentTransaction.replace(R.id.main_content, searchBillFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;

            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(final MenuItem item) {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        Intent reportActivity = new Intent(MainActivity.this, ReportActivity.class);

        switch(item.getItemId()) {
            case R.id.action_login:
                startActivity(intent);
                break;
            case R.id.action_logout:
                startActivity(intent);

                SharedPreferences session = PreferenceManager.
                        getDefaultSharedPreferences(this);

                LoginController loginController =
                        LoginController.getInstance(this);
                loginController.createSessionIsNotLogged(session);
                break;
            case R.id.action_config_deslogged:
                actionDialogNetworkSettings();
                break;
            case R.id.action_config_logged:
                actionDialogNetworkSettings();
                break;
            case R.id.action_notification_logged:
                actionDialogNotification();
                break;

            case R.id.action_share_deslogged:
                shareTextUrl();
                break;
            case R.id.action_share_logged:
                shareTextUrl();
                break;
            case R.id.action_report_logged:
                startActivity(reportActivity);
                break;
            case R.id.action_report_deslogged:
                startActivity(reportActivity);
                break;
        }
        return true;
    }

    private void actionDialogNotification() {
        showDialogConfirmNotificationRequest(MainActivity.this,"Ativar notificação deste projeto",new String[]{"Confirmar"},
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        BillController billcontroller = BillController.getInstance(MainActivity.this);
                        if("-1".equals(billcontroller.getClickedBill())) {
                            Toast.makeText(getBaseContext(), "Selecione um projeto.", Toast.LENGTH_SHORT).show();
                        }else {
                            String response = "";
                            switch (selectedPosition) {

                                case 0:
                                    response = billcontroller.activiteNotification("weekly");

                                    break;
                                case 1:
                                    response = billcontroller.activiteNotification("daily");


                                    break;

                                default:
                                    //Nothing to do
                            }
                            if ("200".equals(response)) {
                                Toast.makeText(getBaseContext(), "Você receberá informações deste projeto.", Toast.LENGTH_SHORT).show();
                            } else {
                                //TODO trocar esta mensagem de erro para quando api estiver funcionando.
                                Toast.makeText(getBaseContext(), "Você receberá informações deste projeto.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void actionDialogNetworkSettings() {
        showDialogNetworkSettings(MainActivity.this, "Download de dados", new String[]{"Confirmar"},
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        SharedPreferences session = PreferenceManager.
                                getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = session.edit();

                        switch(selectedPosition){

                            case 0:
                                editor.putInt(MainActivity.this.getResources()
                                        .getString(R.string.network_settings), 0);
                                break;
                            case 1:
                                editor.putInt(MainActivity.this.getResources()
                                        .getString(R.string.network_settings), 1);
                                break;
                            case 2:
                                editor.putInt(MainActivity.this.getResources()
                                        .getString(R.string.network_settings), 2);
                                break;
                            default:
                                //Nothing to do
                        }
                        editor.commit();
                    }
                });
    }

    public void showDialogNetworkSettings(Context context, String title, String[] btnText,
                                          DialogInterface.OnClickListener listener) {

        final CharSequence[] items = { "Apenas wifi", "Wifi e dados", "Nunca" };

        if (listener == null)
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface,
                                    int paramInt) {
                    paramDialogInterface.dismiss();
                }
            };

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        SharedPreferences session = PreferenceManager.
                getDefaultSharedPreferences(MainActivity.this);
        int networkPreference = session.getInt(MainActivity.this.getResources()
                .getString(R.string.network_settings), 0);

        Log.d("networkPrefe", networkPreference+"");
        builder.setSingleChoiceItems(items, networkPreference,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                    }
                });

        builder.setPositiveButton(btnText[0], listener);

        if (btnText.length != 1) {
            builder.setNegativeButton(btnText[1], listener);
        }
        builder.show();
    }

    public void showDialogConfirmNotificationRequest(Context context, String title, String[] btnText,
                                                     DialogInterface.OnClickListener listener) {

        final CharSequence[] items = { "semanalmente","diariamente"};

        if (listener == null)
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface,
                                    int paramInt) {
                    paramDialogInterface.dismiss();
                }
            };

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        builder.setSingleChoiceItems(items, 1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                    }
                });

        builder.setPositiveButton(btnText[0], listener);

        if (btnText.length != 1) {
            builder.setNegativeButton(btnText[1], listener);
        }
        builder.show();
    }

    @Override
    public void onBackPressed(){
        final CreateSuggestProposal createSuggestProposal = (CreateSuggestProposal)
                getSupportFragmentManager().findFragmentByTag("SUGGEST_PROPOSAL");

        final CreateComment createComment = (CreateComment)
                getSupportFragmentManager().findFragmentByTag("COMMENT_FRAGMENT");

        if(createSuggestProposal != null){
            if(createSuggestProposal.isVisible()){

                EditText proposalSuggestionEditText = (EditText) createSuggestProposal.getView()
                        .findViewById(R.id.suggestionProposalEditText);
                String suggestionTyped = proposalSuggestionEditText.getText().toString();

                if(!suggestionTyped.isEmpty()){

                    confirmDiscard(createSuggestProposal, " sua sugestão?");
                }
                else{
                    super.onBackPressed();
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        }
        else if(createComment != null){
            if(createComment.isVisible()){

                EditText commentEditText = (EditText) createComment.getView()
                        .findViewById(R.id.commentEditText);
                String commentTyped = commentEditText.getText().toString();

                if(!commentTyped.isEmpty()){

                    confirmDiscard(createComment, " seu comentário?");
                }
                else{
                    super.onBackPressed();
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        }
        else{
            super.onBackPressed();
        }
    }

    private void confirmDiscard(final Fragment fragment,
                                String message){

        final AlertDialog.Builder alertDialogProposalBuilder = new AlertDialog.Builder
                (fragment.getContext());

        alertDialogProposalBuilder.setMessage("Você tem certeza que deseja descartar" +
                message);

        alertDialogProposalBuilder.setPositiveButton("Sim", new DialogInterface
                .OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
                getSupportFragmentManager().beginTransaction().remove(fragment)
                        .commit();
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

        alertDialogProposalBuilder.setNegativeButton("Não", new DialogInterface
                .OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });

        alertDialogProposalBuilder.show();
    }


    private void shareTextUrl() {

        final String HOME_PAGE = "http://wikilegis-staging.labhackercd.net/";

        SharedPreferences session = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String link = session.getString(getString(R.string.share_url), HOME_PAGE);

        share.putExtra(Intent.EXTRA_SUBJECT, "Dê uma olhada nisso e fique por dentro das leis:");
        share.putExtra(Intent.EXTRA_TEXT, link);

        startActivity(Intent.createChooser(share, "Compartilhar via"));
    }
/*
    private void openFragment(final Fragment fragmentToBeOpen) {

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_content, fragmentToBeOpen);
        fragmentTransaction.commit();
    }
*/
}
