package com.reobotetechnology.reobotegame.ui.home.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.reobotetechnology.reobotegame.R;
import com.reobotetechnology.reobotegame.adapter.FriendsCircleAdapters;
import com.reobotetechnology.reobotegame.adapter.BooksOfBibleAdapters;
import com.reobotetechnology.reobotegame.adapter.ListHarpeCAdapters;
import com.reobotetechnology.reobotegame.adapter.OracionAdapters;
import com.reobotetechnology.reobotegame.adapter.PersonAdapters;
import com.reobotetechnology.reobotegame.adapter.ThemesVersesOfBibleAdapters;
import com.reobotetechnology.reobotegame.config.ConfigurationFireBase;
import com.reobotetechnology.reobotegame.dao.DataBaseAcess;
import com.reobotetechnology.reobotegame.dao.DataBaseHCAcess;
import com.reobotetechnology.reobotegame.helper.Base64Custom;
import com.reobotetechnology.reobotegame.helper.ConfigurationFirebase;
import com.reobotetechnology.reobotegame.helper.RecyclerItemClickListener;
import com.reobotetechnology.reobotegame.model.OracionModel;
import com.reobotetechnology.reobotegame.model.PersonModel;
import com.reobotetechnology.reobotegame.model.VerseDayModel;
import com.reobotetechnology.reobotegame.model.VersesBibleModel;
import com.reobotetechnology.reobotegame.model.HarpeCModel;
import com.reobotetechnology.reobotegame.model.BooksOfBibleModel;
import com.reobotetechnology.reobotegame.model.Message;
import com.reobotetechnology.reobotegame.model.Notification;
import com.reobotetechnology.reobotegame.model.ThemesModel;
import com.reobotetechnology.reobotegame.model.UserModel;
import com.reobotetechnology.reobotegame.ui.bible.DetailsBookActivity;
import com.reobotetechnology.reobotegame.ui.friends.FriendsListActivity;
import com.reobotetechnology.reobotegame.ui.bible.BibleThemesListActivity;
import com.reobotetechnology.reobotegame.ui.bible.ThemesActivity;
import com.reobotetechnology.reobotegame.ui.bible.ListBiblieScreen;
import com.reobotetechnology.reobotegame.ui.bible.BiblieActivity;
import com.reobotetechnology.reobotegame.ui.friends.FriendProfileActivity;
import com.reobotetechnology.reobotegame.ui.harpe.HarpeActivity;
import com.reobotetechnology.reobotegame.ui.harpe.HarpeListActivity;
import com.reobotetechnology.reobotegame.ui.match.MatchLoadingIAActivity;
import com.reobotetechnology.reobotegame.ui.match.MatchRulesActivity;
import com.reobotetechnology.reobotegame.ui.notifications.NotificationsActivity;
import com.reobotetechnology.reobotegame.ui.match.MatchLoadingActivity;
import com.reobotetechnology.reobotegame.ui.oracion.OracionListActivity;
import com.reobotetechnology.reobotegame.ui.persons.PersonsListActivity;
import com.reobotetechnology.reobotegame.utils.ChecarSegundoPlano;
import com.reobotetechnology.reobotegame.utils.GeneratorID;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    //SwipeRefresh
    private SwipeRefreshLayout swipeRefresh;


    //Toolbar
    private CircleImageView profileImage;
    private TextView textWelcome, textDescriptionNotifications;
    private Button btn_notifications;


    //palavra do dia
    private int livro, capitulo, versiculo;
    private String nm_livro;
    private TextView txtPalavra, txtVerso;
    private ImageView palavreDay;
    private ProgressBar progressBar;
    private ConstraintLayout constraintPrincipal;

    //Configurações do banco de dados
    private FirebaseAuth autenticacao = ConfigurationFireBase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfigurationFireBase.getFirebaseDataBase();
    private FirebaseUser user = autenticacao.getCurrentUser();


    //Amigos Jogar
    private FriendsCircleAdapters adapter;
    private ArrayList<UserModel> lista = new ArrayList<>();

    private BottomSheetDialog bottomSheetDialog;


    //Biblia

    //A.T
    private BooksOfBibleAdapters adapterAntigo;
    private List<BooksOfBibleModel> listaAntigo = new ArrayList<>();

    //N.T
    private BooksOfBibleAdapters adapterNovo;
    private List<BooksOfBibleModel> listaNovo = new ArrayList<>();
    private int tamanho = 0;

    // HARPA CRISTÃ
    private ListHarpeCAdapters adapterHC;
    private List<HarpeCModel> listHC = new ArrayList<>();

    //ic_theme
    private ThemesVersesOfBibleAdapters adapterTheme;
    private List<ThemesModel> listThemes = new ArrayList<>();

    //Oracion

    private OracionAdapters adapterOracion;
    private List<OracionModel> listOracion = new ArrayList<>();

    //Person
    private PersonAdapters adapterPerson;
    private List<PersonModel> listPerson = new ArrayList<>();


    //Vem da Modal Welcome
    private Animation modal_animate;


    //AdMob
    private AdView mAdView;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        mAdView = root.findViewById(R.id.adView);

        progressBar = root.findViewById(R.id.progressBar3);
        constraintPrincipal = root.findViewById(R.id.constraintPrincipal);


        swipeRefresh = root.findViewById(R.id.swipe);

        progressBar.setVisibility(View.VISIBLE);
        constraintPrincipal.setVisibility(View.GONE);

        //Profile
        profileImage = root.findViewById(R.id.profile);
        textWelcome = root.findViewById(R.id.textWelcome);
        textDescriptionNotifications = root.findViewById(R.id.textDescriptionNotifications);
        btn_notifications = root.findViewById(R.id.btn_notifications);

        btn_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });

        textDescriptionNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });


        //configuracoes de objetos
        autenticacao = ConfigurationFirebase.getFirebaseAutenticacao();
        user = autenticacao.getCurrentUser();

        //ModalWelcome
        //Animação da Modal de Tempo Esgotado
        modal_animate = AnimationUtils.loadAnimation(getActivity(), R.anim.modal_animation);

        //Ler na Biblia
        txtPalavra = root.findViewById(R.id.txtPalavra);
        txtVerso = root.findViewById(R.id.txtVerso);

        //Refresh
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        onStart();
                        swipeRefresh.setRefreshing(false);
                    }
                }, 2000);

            }
        });

        //PALAVRA DO DIA
        palavreDay = root.findViewById(R.id.palavreDay);


        //Copiar e Compartilhar
        Button buttonCopy = root.findViewById(R.id.btnCopy);
        Button buttonShare = root.findViewById(R.id.btnShare);

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {


                ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Versiculos", txtPalavra.getText() + "\n\n" + txtVerso.getText());
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(clipData);

                Alerter.create(requireActivity())
                        .setTitle("Obaa...")
                        .setText("Texto copiado para a área de transferência!")
                        .setIcon(R.drawable.ic_success)
                        .setDuration(2000)
                        .setBackgroundColorRes(R.color.colorGreen1)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Alerter.hide();
                            }
                        })
                        .show();

            }
        });


        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_TEXT,
                        txtPalavra.getText() + "\n\n" + txtVerso.getText());

                startActivity(Intent.createChooser(share, "Compartilhar"));
            }
        });


        txtPalavra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLearnBible();
            }
        });

        txtVerso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLearnBible();
            }
        });

        //EVENTOS VerMais de Recycler Horizontal

        TextView textViewMoreFriends = root.findViewById(R.id.txtViewMoreFriends);
        TextView txtViewMoreThemes = root.findViewById(R.id.txtViewMoreVerses);
        TextView txtViewMoreBackTestament = root.findViewById(R.id.txtViewMoreBackTestament);
        TextView txtViewMoreNewTestament = root.findViewById(R.id.txtViewMoreNewTestament);
        TextView txtViewMoreHarpe = root.findViewById(R.id.txtViewMoreHarpe);
        TextView txtViewMoreOracion = root.findViewById(R.id.txtViewMoreOracion);
        TextView txtViewMorePerson = root.findViewById(R.id.txtViewMorePerson);


        textViewMoreFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FriendsListActivity.class);
                i.putExtra("eventList", getString(R.string.seguidoresMin));
                startActivity(i);
            }
        });

        txtViewMoreThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BibleThemesListActivity.class);
                startActivity(i);
            }
        });


        txtViewMoreBackTestament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListBiblieScreen.class);
                i.putExtra("cd_testamento", 0);
                startActivity(i);
            }
        });

        txtViewMoreNewTestament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListBiblieScreen.class);
                i.putExtra("cd_testamento", 1);
                startActivity(i);
            }
        });

        txtViewMoreHarpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), HarpeListActivity.class);
                startActivity(i);
            }
        });

        txtViewMoreOracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), OracionListActivity.class);
                startActivity(i);
            }
        });

        txtViewMorePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PersonsListActivity.class);
                startActivity(i);
            }
        });


        //Configurações Recycler

        RecyclerView recyclerAmigos = root.findViewById(R.id.recyclerAmigos);
        RecyclerView recyclerThemes = root.findViewById(R.id.recyclerThemesVerses);
        RecyclerView recyclerAntigoTestamento = root.findViewById(R.id.recyclerLivrosAntigo);
        RecyclerView recyclerNovoTestamento = root.findViewById(R.id.recyclerLivrosNovo);
        RecyclerView recyclerHC = root.findViewById(R.id.recyclerHC);
        RecyclerView recyclerOracion = root.findViewById(R.id.recyclerOracion);
        RecyclerView recyclerPerson = root.findViewById(R.id.recyclerPerson);


        //configurarAdapter

        adapter = new FriendsCircleAdapters(lista, getActivity());
        adapterTheme = new ThemesVersesOfBibleAdapters(listThemes, getActivity());
        adapterAntigo = new BooksOfBibleAdapters(listaAntigo, getActivity());
        adapterNovo = new BooksOfBibleAdapters(listaNovo, getActivity());
        adapterHC = new ListHarpeCAdapters(listHC, getActivity());
        adapterOracion = new OracionAdapters(listOracion, getActivity());
        adapterPerson = new PersonAdapters(listPerson, getActivity());

        //Configuraçoes de Layout do Recycler

        //RecyclerAmigosJogar
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerAmigos.setLayoutManager(layoutManager);
        recyclerAmigos.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerThemes.setLayoutManager(layoutManager4);
        recyclerThemes.setAdapter(adapterTheme);

        //RecyclerLivros
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerAntigoTestamento.setLayoutManager(layoutManager1);
        recyclerAntigoTestamento.setAdapter(adapterAntigo);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerNovoTestamento.setLayoutManager(layoutManager2);
        recyclerNovoTestamento.setAdapter(adapterNovo);

        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerHC.setLayoutManager(layoutManager3);
        recyclerHC.setAdapter(adapterHC);

        RecyclerView.LayoutManager layoutManager6 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerOracion.setLayoutManager(layoutManager6);
        recyclerOracion.setAdapter(adapterOracion);

        RecyclerView.LayoutManager layoutManager5 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerPerson.setLayoutManager(layoutManager5);
        recyclerPerson.setAdapter(adapterPerson);


        //Eventos de Clique

        //aMIGOS jOGAR
        recyclerAmigos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerAmigos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onItemClick(View view, int position) {

                                final UserModel usuarioSelecionado = lista.get(position);

                                String idUsuario = Base64Custom.codificarBase64(Objects.requireNonNull(user.getEmail()));
                                firebaseRef.child("usuarios").child(idUsuario).addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        UserModel user = dataSnapshot.getValue(UserModel.class);
                                        if (user != null) {
                                            if (!user.isLearnRules()) {
                                                getRules();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                if (usuarioSelecionado.getNome().equals("Amigo")) {
                                    Intent i = new Intent(getActivity(), FriendsListActivity.class);
                                    i.putExtra("eventList", getString(R.string.amigos_sugeridosM));
                                    startActivity(i);
                                } else {
                                    showBottomSheetPickPhoto(usuarioSelecionado);
                                }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        //ThemesVerse
        recyclerThemes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerThemes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                ThemesModel themes = listThemes.get(position);

                                Intent i = new Intent(getActivity(), ThemesActivity.class);
                                i.putExtra("theme", themes.getThemeText());
                                requireActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );


        //Recycler Antigo
        recyclerAntigoTestamento.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerAntigoTestamento,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                tamanho = listaAntigo.size();

                                if (tamanho > 2) {

                                    BooksOfBibleModel livroSelecionado = listaAntigo.get(position);
                                    Intent i = new Intent(getActivity(), DetailsBookActivity.class);
                                    i.putExtra("nm_book", livroSelecionado.getNome());
                                    i.putExtra("livroSelecionado", livroSelecionado.getId());
                                    startActivity(i);
                                }


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        //Recycler Novo
        recyclerNovoTestamento.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerNovoTestamento,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                tamanho = listaNovo.size();

                                if (tamanho > 2) {

                                    BooksOfBibleModel livroSelecionado = listaNovo.get(position);
                                    Intent i = new Intent(getActivity(), DetailsBookActivity.class);
                                    i.putExtra("nm_book", livroSelecionado.getNome());
                                    i.putExtra("livroSelecionado", livroSelecionado.getId());
                                    startActivity(i);

                                }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );


        //Recycler Harpa
        recyclerHC.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerHC,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                //clicar
                                HarpeCModel selected = listHC.get(position);
                                Intent i = new Intent(getActivity(), HarpeActivity.class);
                                i.putExtra("id", selected.getId());
                                i.putExtra("title", selected.getTitle());
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        createMessageIA();

        return root;
    }


    //Profile

    //Usuario Logado
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void viewProfile() {

        if (autenticacao.getCurrentUser() != null) {

            try {
                if (user.getPhotoUrl() == null) {
                    Glide
                            .with(HomeFragment.this)
                            .load(R.drawable.profile)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(profileImage);
                } else {
                    Glide
                            .with(HomeFragment.this)
                            .load(user.getPhotoUrl())
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(profileImage);
                }
            } catch (Exception e) {
                Glide
                        .with(HomeFragment.this)
                        .load(R.drawable.profile)
                        .centerCrop()
                        .placeholder(R.drawable.profile)
                        .into(profileImage);

            }


            try {

                final String msg = "Olá, " + user.getDisplayName();

                textWelcome.setText(msg);

                final String idUsuario = Base64Custom.codificarBase64(Objects.requireNonNull(user.getEmail()));
                firebaseRef.child("usuarios").child(idUsuario).addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserModel user = dataSnapshot.getValue(UserModel.class);

                        //Verificar se é a primeira vez do usuário acessando o jogo

                        assert user != null;

                        if (!user.isFirstAcessed()) {

                            try {

                                boolean foregroud = new ChecarSegundoPlano().execute(getActivity()).get();
                                if (foregroud) {
                                    modalWelcome(msg);
                                    DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
                                    usuarioRef.child("firstAcessed").setValue(true);
                                    usuarioRef.child("pontosG").setValue(20);
                                }

                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } catch (Exception ignored) {

            }


        }
    }

    private void modalWelcome(String msg) {

        final Dialog welcomeModal = new Dialog(HomeFragment.this.requireActivity());
        welcomeModal.requestWindowFeature(Window.FEATURE_NO_TITLE);
        welcomeModal.setCancelable(false);
        welcomeModal.setContentView(R.layout.include_modal);

        CardView cardModal = welcomeModal.findViewById(R.id.cardModal);
        cardModal.startAnimation(modal_animate);
        ImageButton btn_close = welcomeModal.findViewById(R.id.btn_close);
        Button btnAction = welcomeModal.findViewById(R.id.btnAction);
        TextView txt_title = welcomeModal.findViewById(R.id.txt_title);
        TextView txtDescription = welcomeModal.findViewById(R.id.txtDescription);

        txt_title.setText(msg);
        txtDescription.setText(getString(R.string.descriptionWelcome));

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welcomeModal.dismiss();
            }
        });
        cardModal.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                welcomeModal.dismiss();
                welcomeModal.dismiss();
                welcomeModal.hide();
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                welcomeModal.dismiss();
                welcomeModal.dismiss();
                welcomeModal.hide();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(welcomeModal.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        welcomeModal.setCancelable(true);

        welcomeModal.show();

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getAllNotifications() {

        final String idUsuario = Base64Custom.codificarBase64(Objects.requireNonNull(user.getEmail()));
        firebaseRef.child("notifications").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                try {

                    int countNoitificationsView = 0;

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {

                        Notification notification = new Notification();
                        String view = Objects.requireNonNull(dados.child("view").getValue()).toString();
                        notification.setView(Boolean.parseBoolean(view));
                        if (!notification.isView()) {
                            countNoitificationsView = countNoitificationsView + 1;
                        }


                    }

                    textDescriptionNotifications.setText(Html.fromHtml("Você tem <b>" + countNoitificationsView + "</b> nova(s) notificaçoes"));
                    btn_notifications.setText("" + countNoitificationsView);

                } catch (Exception e) {
                    //Log.i("ERRO NOTIFICATION", Objects.requireNonNull(e.getMessage()));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //Palavra do dia
    private Integer livro() {

        int book = 0;
        try {
            DataBaseAcess dataBaseAcess = DataBaseAcess.getInstance(getActivity());
            List<BooksOfBibleModel> livroLista = dataBaseAcess.listarLivroPalavra();
            BooksOfBibleModel p = livroLista.get(0);
            if (p.getId() != 0) {
                book = p.getId();
            }
        } catch (Exception ignored) {

        }

        return book;

    }

    private Integer capitulo(int book) {

        int chapther = 0;

        try {
            DataBaseAcess dataBaseAcess = DataBaseAcess.getInstance(getActivity());
            chapther = dataBaseAcess.capituloPalavra(book);

        } catch (Exception ignored) {
        }

        return chapther;


    }

    @SuppressLint("SetTextI18n")
    private void verseOfDay() {

        try {

            DataBaseAcess dataBaseAcess = DataBaseAcess.getInstance(getActivity());
            List<VerseDayModel> listVerseDay = dataBaseAcess.listVerseDay();

            if (listVerseDay.size() != 0) {

                String date = listVerseDay.get(0).getDate();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatNotification = new SimpleDateFormat("dd-MM-yyyy");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                Calendar cal = Calendar.getInstance();
                Date data = new Date();
                cal.setTime(data);
                Date data_atual = cal.getTime();

                String dateNotification = dateFormatNotification.format(data);
                String time = timeFormat.format(data_atual);

                String[] dateSplit = dateNotification.split("-");
                String[] dateDataBaseSplit = date.split("-");

                int yearDateSplit = Integer.parseInt(dateSplit[2]);
                int monthDateSplit = Integer.parseInt(dateSplit[1]);
                int dayDateSplit = Integer.parseInt(dateSplit[0]);

                int yearDataBaseSplit = Integer.parseInt(dateDataBaseSplit[2]);
                int monthDataBaseSplit = Integer.parseInt(dateDataBaseSplit[1]);
                int dayDataBaseSplit = Integer.parseInt(dateDataBaseSplit[0]);

                String[] timeNow = time.split(":");
                int timeNowHours = Integer.parseInt(timeNow[0]);


                boolean findVerse = false;

                //Se faz mais de um ano que o usuário não acessa o app já carrega um novo verso
                if (yearDateSplit > yearDataBaseSplit) {
                    findVerse = true;
                }
                //Se faz menos de um ano vamos pra validação
                else if (yearDateSplit == yearDataBaseSplit) {
                    //Se faz mais de um mês que o usuário não acessa o app já carrega um novo verso
                    if (monthDateSplit > monthDataBaseSplit) {
                        findVerse = true;
                        //Se faz mais de um dois que o usuário não acessa o app já carrega um novo verso
                    } else if (dayDateSplit > (dayDataBaseSplit + 1)) {
                        findVerse = true;
                        //Se o usuário acessou o app ontem (Verifica se já passou das 8 demanhã de hoje) se passou.... Carrega um novo Verso.
                        //Senão continua com o mesmo verso de ontem até passar das 8 da manhã
                    } else if (dayDateSplit > dayDataBaseSplit) {
                        //se (timeNowHours >= 8:00 da manhã...) traz a palavra do dia
                        if (timeNowHours >= 8) {
                            findVerse = true;
                            //sendNotificationAlertVerseOfDayforUserLogged
                        }

                    }
                }

                //Se for necessário buscar outro verso no banco senão usar o verso que já está no banco
                if (findVerse) {
                    findNewVerse();

                } else {

                    int randomImageVerseDay = new Random().nextInt(2);

                    if (randomImageVerseDay == 0) {
                        palavreDay.setImageResource(R.drawable.verse_day);
                    } else {
                        palavreDay.setImageResource(R.drawable.verse_day2);
                    }

                    int book = listVerseDay.get(0).getBook_id();
                    String nm_book = dataBaseAcess.findBook(book);
                    int chapther = listVerseDay.get(0).getChapter_id();
                    int verse = listVerseDay.get(0).getVerse_id();

                    String text = dataBaseAcess.findVerses(book, chapther, verse);

                    //Tamanho do versículo for maior que 150 não precisa pesquisar outro no banco
                    //Senão busca o próximo se tiver
                    if (text.length() > 150) {
                        txtPalavra.setText(text);
                        String nm_versiculo = nm_book + " " + chapther + ":" + verse;
                        txtVerso.setText(nm_versiculo);

                        versiculo = verse;

                    } else {
                        try {

                            int verse2;
                            verse2 = (verse + 1);

                            String text2;
                            text2 = dataBaseAcess.findVerses(book, chapther, verse2);

                            //Verifica se existe o próximo versículo no msm capítulo (verse2 = (verse+1))
                            //Senão existir pega o versículo anterior (verse2 = (verse-1))

                            if (text2.isEmpty()) {
                                verse2 = (verse - 1);
                                text2 = dataBaseAcess.findVerses(book, chapther, verse2);

                                txtPalavra.setText(text2 + text);
                                String nm_versiculo = nm_book + " " + chapther + ":" + verse2 + "," + verse;
                                txtVerso.setText(nm_versiculo);

                                versiculo = verse2;
                            } else {
                                txtPalavra.setText(text + text2);
                                String nm_versiculo = nm_book + " " + chapther + ":" + verse + "," + verse2;
                                txtVerso.setText(nm_versiculo);

                                versiculo = verse;
                            }

                            if (txtPalavra.getText().length() < 150) {

                                int verse3;
                                verse3 = (verse2 + 1);

                                String text3;
                                text3 = dataBaseAcess.findVerses(book, chapther, verse3);

                                if (text3.isEmpty()) {
                                    verse3 = (verse - 2);
                                    text3 = dataBaseAcess.findVerses(book, chapther, verse3);

                                    txtPalavra.setText(text3 + text2 + text);
                                    String nm_versiculo = nm_book + " " + chapther + ":" + verse3 + "," + verse2 + "," + verse;
                                    txtVerso.setText(nm_versiculo);

                                    versiculo = verse3;
                                } else {
                                    txtPalavra.setText(text + text2 + text3);
                                    String nm_versiculo = nm_book + " " + chapther + ":" + verse + "," + verse2 + "," + verse3;
                                    txtVerso.setText(nm_versiculo);

                                    versiculo = verse;
                                }

                            }


                        } catch (Exception ignored) {
                        }

                    }

                    //SendNextActionOnCLikinVerseText
                    livro = book;
                    nm_livro = nm_book;
                    capitulo = chapther;

                }


            } else {
                txtPalavra.setText(getString(R.string.versiculo));
                txtVerso.setText(getString(R.string.verso_cap_livro));

            }

        } catch (Exception ignored) {
            txtPalavra.setText(getString(R.string.versiculo));
            txtVerso.setText(getString(R.string.verso_cap_livro));

        }


    }

    private void findNewVerse() {

        try {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatNotification = new SimpleDateFormat("dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            Calendar cal = Calendar.getInstance();
            Date data = new Date();
            cal.setTime(data);
            Date data_atual = cal.getTime();

            String dateNotification = dateFormatNotification.format(data);
            String time = timeFormat.format(data_atual);

            int book = livro();
            int chapther = capitulo(book);

            DataBaseAcess dataBaseAcess = DataBaseAcess.getInstance(getActivity());
            List<VersesBibleModel> versiculoLista = dataBaseAcess.listarVersiculoPalavra(book, chapther);
            VersesBibleModel v = versiculoLista.get(0);

            int verse = v.getVerso();

            List<VerseDayModel> listVerseConsult = new ArrayList<>();

            listVerseConsult.add(new VerseDayModel(1, book, chapther, verse, dateNotification, time));

            DataBaseAcess dataBaseAcessUpdate = DataBaseAcess.getInstance(getActivity());

            dataBaseAcessUpdate.updateVerseDay(listVerseConsult);

            this.verseOfDay();


        } catch (Exception ignored) {

        }

    }

    private void openLearnBible() {
        if (livro != 0) {
            Intent i = new Intent(getActivity(), BiblieActivity.class);
            i.putExtra("nm_livro", nm_livro);
            i.putExtra("livroSelecionado", livro);
            i.putExtra("capitulo", capitulo);
            i.putExtra("versiculo", versiculo);
            startActivity(i);
        }
    }

    //ListasRecycler
    private void listarAmigos() {

        firebaseRef.child("usuarios").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                lista.clear();
                UserModel usuarioModel = new UserModel();
                usuarioModel.setNome("Amigo");
                usuarioModel.setJogando(false);
                usuarioModel.setOnline(false);
                lista.add(usuarioModel);

                UserModel usuarioModel2 = new UserModel();
                usuarioModel2.setNome("Reobote IA");
                usuarioModel2.setJogando(false);
                usuarioModel2.setOnline(true);

                lista.add(usuarioModel2);

                for (DataSnapshot dados : dataSnapshot.getChildren()) {

                    try {
                        UserModel usuario2Model = dados.getValue(UserModel.class);

                        assert usuario2Model != null;
                        if (!usuario2Model.getEmail().equals(user.getEmail())) {
                            lista.add(usuario2Model);
                        }
                    } catch (Exception e) {
                        Log.i("ERRO: ", Objects.requireNonNull(e.getMessage()));
                    }

                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showBottomSheetPickPhoto(final UserModel userInvite) {


        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.include_bottom_sheet_invite_friend, null);

        TextView user1 = view.findViewById(R.id.textView20);
        String[] nome = Objects.requireNonNull(user.getDisplayName()).split(" ");
        user1.setText(nome[0]);

        final TextView rankingUser = view.findViewById(R.id.textView26);


        String idUsuario = Base64Custom.codificarBase64(Objects.requireNonNull(user.getEmail()));

        firebaseRef.child("usuarios").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null) {
                    rankingUser.setText(user.getRanking() + "º");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        CircleImageView profile = view.findViewById(R.id.profile);

        Glide
                .with(view)
                .load(user.getPhotoUrl())
                .centerCrop()
                .placeholder(R.drawable.profile)
                .into(profile);


        //User2

        TextView user2 = view.findViewById(R.id.textView21);
        TextView rankingUser2 = view.findViewById(R.id.textView30);
        CircleImageView profile2 = view.findViewById(R.id.profile2);

        if (userInvite.getNome().equals(getString(R.string.name_robot))) {

            String nome2 = userInvite.getNome();
            user2.setText(nome2);
            rankingUser2.setText(userInvite.getRanking() + "º");


            Glide
                    .with(view)
                    .load(R.drawable.reobote)
                    .centerCrop()
                    .placeholder(R.drawable.profile)
                    .into(profile2);

            view.findViewById(R.id.button9).setVisibility(View.GONE);

            view.findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), MatchLoadingIAActivity.class);
                    i.putExtra("name", getString(R.string.name_robot));
                    startActivity(i);
                    bottomSheetDialog.dismiss();
                }
            });

        } else {


            String[] nome2 = userInvite.getNome().split(" ");
            user2.setText(nome2[0]);


            rankingUser2.setText(userInvite.getRanking() + "º");

            Glide
                    .with(view)
                    .load(userInvite.getImagem())
                    .centerCrop()
                    .placeholder(R.drawable.profile)
                    .into(profile2);

            view.findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (userInvite.isJogando()) {
                        //Se usuario estiver jogando
                        //Modal de Aviso que usuário já está em outra partida
                        alert("Jogador(a): " + userInvite.getNome() + " está em uma partida no momento. Aguarde 5 minutos ou convide outro amigo");
                    } else {

                        Intent i = new Intent(view.getContext(), MatchLoadingActivity.class);
                        i.putExtra("token", userInvite.getToken());
                        i.putExtra("email", userInvite.getEmail());
                        i.putExtra("convidado", "nao");
                        i.putExtra("idPartida", "");
                        startActivity(i);
                    }

                    bottomSheetDialog.dismiss();
                }
            });


            view.findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getActivity(), FriendProfileActivity.class);
                    i.putExtra("id", userInvite.getEmail());
                    startActivity(i);
                    bottomSheetDialog.dismiss();
                }
            });

        }


        view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });

        bottomSheetDialog.show();
    }

    private void listThemesVerses() {

        listThemes.clear();

        listThemes.add(new ThemesModel("Amor"));
        listThemes.add(new ThemesModel("Amizade"));
        listThemes.add(new ThemesModel("Ansiedade"));
        listThemes.add(new ThemesModel("Namoro"));
        listThemes.add(new ThemesModel("Casamento"));
        listThemes.add(new ThemesModel("Santidade"));
        listThemes.add(new ThemesModel("Pecado"));
        listThemes.add(new ThemesModel("Tristeza"));
        listThemes.add(new ThemesModel("Sabedoria"));
        listThemes.add(new ThemesModel("Aprender"));
        listThemes.add(new ThemesModel("Oração"));

        adapterTheme.notifyDataSetChanged();


    }

    private void listarLivros() {

        listaAntigo.clear();
        listaNovo.clear();

        DataBaseAcess dataBaseAcess = DataBaseAcess.getInstance(getActivity());
        List<BooksOfBibleModel> lista2;
        lista2 = dataBaseAcess.listarAntigoTestamento();

        if (lista2.size() != 0) {
            for (int i = 0; i < lista2.size(); i++) {

                int learningBook = dataBaseAcess.learningBook(lista2.get(i).getId());
                boolean favorited = dataBaseAcess.favorited(lista2.get(i).getId());
                lista2.get(i).setLearning(learningBook);
                lista2.get(i).setFavorited(favorited);
                listaAntigo.add(lista2.get(i));
            }
        }

        List<BooksOfBibleModel> lista3;
        lista3 = dataBaseAcess.listarNovoTestamento();

        if (lista3.size() != 0) {
            for (int i = 0; i < lista3.size(); i++) {

                int learningBook = dataBaseAcess.learningBook(lista3.get(i).getId());
                boolean favorited = dataBaseAcess.favorited(lista3.get(i).getId());
                lista3.get(i).setLearning(learningBook);
                lista3.get(i).setFavorited(favorited);
                listaNovo.add(lista3.get(i));
            }
        }

        adapterAntigo.notifyDataSetChanged();
        adapterNovo.notifyDataSetChanged();


    }

    private void listHarpeC() {

        listHC.clear();

        DataBaseHCAcess dataBaseHCAcess = DataBaseHCAcess.getInstance(getActivity());
        List<HarpeCModel> livroLista = dataBaseHCAcess.listHC();
        if (livroLista.size() != 0) {
            listHC.addAll(livroLista);
        }

        adapterHC.notifyDataSetChanged();
    }

    private void listOracion(){

        listOracion.clear();

        listOracion.add(new OracionModel("1", "21 Dias de Daniel", "", "03/06/21", "24/06/21", "23:00 - 00:00", "Em andamento", 10));
        listOracion.add(new OracionModel("2", "150 Dias de Salmos", "", "09/06/21", "31/08/21", "23:00 - 00:00", "Aguardando", 10));
        listOracion.add(new OracionModel("3", "12 Dias de Gratidão", "", "05/06/21", "1/06/21", "23:00 - 00:00", "Em andamento", 10));

        adapterOracion.notifyDataSetChanged();
    }

    private void listPerson() {

        listPerson.clear();

        listPerson.add(new PersonModel("1", "Elias", ""));
        listPerson.add(new PersonModel("2", "Moises", ""));
        listPerson.add(new PersonModel("3", "Noé", ""));
        listPerson.add(new PersonModel("1", "Elias", ""));
        listPerson.add(new PersonModel("2", "Moises", ""));
        listPerson.add(new PersonModel("3", "Noé", ""));

        adapterPerson.notifyDataSetChanged();

    }


    private void atualizarRanking() {
        final List<UserModel> listaRankingAll = new ArrayList<>();


        try {

            firebaseRef.child("usuarios").orderByChild("pontosG").addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    listaRankingAll.clear();
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {

                        UserModel usuario2Model = dados.getValue(UserModel.class);

                        listaRankingAll.add(usuario2Model);
                    }

                    int tamanho = listaRankingAll.size();

                    for (int i = 0; i < tamanho; i++) {
                        int pos = (i + 1);
                        UserModel usuarioSelecionado = listaRankingAll.get((tamanho - i - 1));
                        String idUsuario = Base64Custom.codificarBase64((Objects.requireNonNull(usuarioSelecionado.getEmail())));
                        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
                        usuarioRef.child("backPosition").setValue(usuarioSelecionado.getRanking());
                        usuarioRef.child("ranking").setValue(pos);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            constraintPrincipal.setVisibility(View.VISIBLE);
                        }
                    }, 1000);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception ignored) {

        }



    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createMessageIA() {

        try {

            final String idUsuario = Base64Custom.codificarBase64(Objects.requireNonNull(user.getEmail()));
            firebaseRef.child("usuarios").child(idUsuario).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    UserModel user = dataSnapshot.getValue(UserModel.class);

                    assert user != null;
                    if (!user.isDayMessageIA()) {
                        String emailUsuario = Objects.requireNonNull("reobote@gmail.com");
                        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

                        String emailUsuario2 = Objects.requireNonNull(user.getEmail());
                        String idUsuario2 = Base64Custom.codificarBase64(emailUsuario2);

                        long timestamp = System.currentTimeMillis();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatNotification = new SimpleDateFormat("dd-MM-yyyy");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                        Calendar cal = Calendar.getInstance();
                        Date data = new Date();
                        cal.setTime(data);
                        Date data_atual = cal.getTime();

                        String dateNotification = dateFormatNotification.format(data);
                        String time = timeFormat.format(data_atual);

                        //Cria uma partida
                        String idMessage = dateFormat.format(data_atual);

                        final Message message = new Message();
                        message.setFromId(idUsuario);
                        message.setToId(idUsuario2);
                        message.setTimestamp("" + timestamp);
                        message.setText("Lhe enviou uma mensagem");


                        Notification notification = new Notification();
                        notification.setFromId(message.getFromId());
                        notification.setToId(message.getToId());
                        notification.setTimestamp(message.getTimestamp());
                        notification.setText(message.getText());
                        notification.setTipo("chat");
                        notification.setFromName(getString(R.string.name_robot));
                        notification.setId(idMessage);
                        notification.setFromImage("");

                        notification.setDate(dateNotification);
                        notification.setTime(time);

                        notification.setView(false);
                        DatabaseReference usuarioRef = firebaseRef.child("notifications");
                        usuarioRef.child(idUsuario2).child("" + timestamp).setValue(notification);

                        DatabaseReference userRefDB = firebaseRef.child("usuarios").child(idUsuario2);
                        userRefDB.child("dayMessageIA").setValue(true);

                        Message m = new Message();
                        String id = GeneratorID.id();
                        m.setId(id);
                        m.setText(Base64Custom.codificarBase64("Olá, "+user.getNome()+". Tudo bem? Eu sou a inteligência artifical do Reobote Game. É um prazer te conhecer.\n\nVocê é uma Bênção"));
                        String date_time = dateNotification+"/"+time;
                        m.setTimestamp(date_time);
                        m.setFromId(Base64Custom.codificarBase64(getString(R.string.email_robot)));
                        m.setToId(Base64Custom.codificarBase64(Objects.requireNonNull(user.getEmail())));
                        m.setView(true);
                        m.setType(1);
                        m.save();



                    }else {
                        //sorteia mensagens diarias
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception ignored) {

        }


    }

    private void alert(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    private void getRules() {
        new SweetAlertDialog(this.requireActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Regras")
                .setContentText("Deseja ler as regras do jogo antes de enviar o convite ?")
                .setConfirmText("Sim")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(getActivity(), MatchRulesActivity.class));
                        try {
                            String idUsuario = Base64Custom.codificarBase64((Objects.requireNonNull(user.getEmail())));
                            DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
                            usuarioRef.child("learnRules").setValue(true);
                        } catch (Exception ignored) {

                        }
                        sDialog.hide();
                    }
                }).setCancelText("Não")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        try {
                            String idUsuario = Base64Custom.codificarBase64((Objects.requireNonNull(user.getEmail())));
                            DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
                            usuarioRef.child("learnRules").setValue(true);
                        } catch (Exception ignored) {

                        }
                        sweetAlertDialog.hide();
                    }
                })
                .show();
    }

    private void loadBannerAdMob() {
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }, 1200);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        viewProfile();
        getAllNotifications();
        verseOfDay();
        listarAmigos();
        listThemesVerses();
        listarLivros();
        listHarpeC();
        listOracion();
        listPerson();
        atualizarRanking();
        loadBannerAdMob();
        super.onStart();
    }

}
