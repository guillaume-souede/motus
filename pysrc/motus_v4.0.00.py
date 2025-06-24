#!/usr/bin/python3
# coding: utf-8
"""
MOTUS - Une étude Python POO , adaptation du jeu télévisé "MOTUS"
sur France Télévision en mode graphique (TKinter) et Python 3.8.10
Copyright (C) 2025  Bernard AMOUROUX

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

MOTUS v4.0 (C) may 2025  Bernard AMOUROUX
This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
This is free software, and you are welcome to redistribute it
under certain conditions; type `show c' for details.
"""

__author__ = "Bernard AMOUROUX"
__date__ = "$Date: 2025/05/18 07:00 $"
__copyright__ = "Copyright (c) 2025 Bernard AMOUROUX"
__license__ = "GPL 3"

import sys,os
import os.path as op
import tkinter as tk
import tkinter.font as tkFont

from handledico import Handle_DicoMotus
from unicodedata import normalize,category
from computer import IA_Computer
from gameboard import GameBoard
from human import Human_Player
#from time import sleep

from gui_tools import *
from configs import *


class Application(tk.Tk):
    """ Classe qui gère l'affichage et la gestion du jeu en lui-même
        Paramètre:
            'filename': nom du fichier des mots à charger servant de dictionnaire de référence
                        peut-être passé en paramètre de ligne de commande (nom complet)
    """
    def __init__(self, filename:str):
        
        tk.Tk.__init__(self, className="Application")
        # ------- Chargement du fichier des paramètres de l'application -------
        self.app_Parameters = Saveload_CFG()
        if self.app_Parameters.cfg_load():
            background = self.app_Parameters.options.backfilename
            imgpath = self.app_Parameters.options.imagepath
        else:
            exit(1)
        # -------------- Initialisation des images de fond du jeu -------------
        self.backImage = tk.PhotoImage(master=self,file=op.join(os.getcwd(),imgpath,background))
        self.winnerImage = tk.PhotoImage(master=self,file=op.join(os.getcwd(),imgpath,"victoire.png"))
        self.loserImage = tk.PhotoImage(master=self,file=op.join(os.getcwd(),imgpath,"defaite.png"))
        self.abortImage = tk.PhotoImage(master=self,file=op.join(os.getcwd(),imgpath,"dommage.gif"))
        self.selectImage = tk.PhotoImage(master=self,file=op.join(os.getcwd(),imgpath,"tutoriel.png"))
        # ---------- Initialisation des polices de caractères du jeu ----------
        self.labelFont = tkFont.Font(self,family='Courier New',size=11,weight='bold',slant='roman')
        self.menuFont = tkFont.Font(self, family='Serif', size=11, weight='normal', slant='italic')
        # ---------------------------------------------------------------------
        self.__IA_status = ""                   # ---- status du joueur IA : winner/loser/idle
        self.__human_status = ""                # ---- status du joueur Humain : winner/loser/idle
        self.__MOTUS_word:str=""                # ---- le mot à trouver en mode 'Humain vs IA'
        self.__MOTUS_Player:str = self.app_Parameters.options.gamemode   # ---- type du joueur MOTUS, humain ou IA
        self.__dico_Letters:dict = ({})         # ---- dictionnaire de décomposition du mot en lettres
        self.vnbessais = tk.IntVar(value=self.app_Parameters.options.nb_tries)    # ---- nombre de mots proposables pour la partie
        self.vnblettres = tk.IntVar(value=self.app_Parameters.options.nb_letters) # ---- Nombre de lettres du mot MOTUS
        self.vrequest = tk.StringVar(value=f" Votre mot de {self.vnblettres.get()} lettres ...")     # ---- proposition de mot en mode 'human'
        # ---------- Interception de la croix rouge en haut à droite ----------
        self.protocol('WM_DELETE_WINDOW',self.Quit)
        # ---- Taille de la fenètre du jeu fonction de la résolution écran ----
        MAX_WIDTH, MAX_HEIGHT = self.maxsize()  # --- renvoi la taille écran --
        self.app_size = min(MAX_WIDTH-100,self.backImage.width()), min(MAX_HEIGHT-200,self.backImage.height())
        self.minsize(MAX_WIDTH//2, MAX_HEIGHT//2)
        # ----------------- Mise en place plein écran oui/non -----------------
        self.__fullscreen:bool = self.app_Parameters.options.fullscreen
        self.attributes("-fullscreen", self.__fullscreen)
        # ---------------------------------------------------------------------
        #'commandsList': tuple de la forme (label_cmd:str, accel_cmd:str,commande:callable)
        #'nosel'       : list[int] liste des indices des rubrique dont l'état sera 'disabled'
        commandsList = [(" Paramètres de MOTUS","",self.parameters), (" Choix du dictionnaire des mots","",self.select_dictionary),
                        (" Changer Mode de jeu","",self.select_gamemode), (" Difficulté  du jeu","",self.select_difficulty),
                        ("separator","",None),(" Mode plein écran on/off","F11",self.__toggle_fullscreen),
                        ("separator","",None),(" Quitter MOTUS"," Alt-F4 ",self.Quit)]
        self.event_add("<<PopupMenu>>","<Control-M>","<Control-m>","<Button-3>")
        self.popupMenu = Motus_PopupMenu(self, commandsList, nosel=[4])
        self.bind_all("<<PopupMenu>>", self.popupMenu.show_Menu_Popup)
        self.bind("<Escape>", self.__exit_fullscreen)
        self.bind("<F11>",self.__toggle_fullscreen)
        self.bind("<Alt-F4>",self.Quit)
        # ---------------------------------------------------------------------
        self.title(f"MOTUS v3.0\t{'version '+self.app_Parameters.options.difficulty.upper():^20}\t\t(c)AMOUROUX Bernard  Mai 2025")
        [self.columnconfigure(i, weight=1) for i in range(9,41)]
        [self.rowconfigure(i, weight=1) for i in range(41)]
        self.configure(bg='wheat')
        # ---------------------------------------------------------------------
        self.chronometre = Chronometre(self, self.app_Parameters.options.difficulty)
        self.dico_MOTUS = Handle_DicoMotus(self, self.app_Parameters)
        self.game_rules = Game_Rules(self, self.app_Parameters)
        self.messageBox = Win_MessageBox(self)
        self.cree_widgets()
    
    @property
    def dico_Letters(self) -> dict:
        return self.__dico_Letters
    @dico_Letters.setter
    def dico_Letters(self, key:tuple, data:tuple):
        self.__dico_Letters[key] = data
    
    @property
    def MOTUS_word(self) -> str:
        return self.__MOTUS_word
    @MOTUS_word.setter
    def MOTUS_word(self, motusword:str):
        self.__MOTUS_word = motusword
    
    def __exit_fullscreen(self, event:tk.Event=None):
        self.__fullscreen = False
        self.attributes("-fullscreen", self.__fullscreen)

    def __toggle_fullscreen(self, event:tk.Event=None):
        # ---- Contournement du bug Tkinter() ----
        self.after(150, self.deiconify)
        self.withdraw()
        self.update_idletasks()
        self.__fullscreen = not self.__fullscreen
        self.attributes("-fullscreen",self.__fullscreen)
    
    def cree_widgets(self):
        # -----------------------------------------------------------------------------------------
        frameletters = My_LabelFrame(self,bd=2,bg="wheat",cspan=9,pad=(2,2,0,0))
        tk.Label(frameletters, bd=0, bg='wheat',font=self.labelFont,
                              text=" Longueur du mot :").grid(column=0,row=0,columnspan=3,sticky="w")
        self.spboxletters = tk.Spinbox(frameletters,bd=2,relief='sunken',textvariable=self.vnblettres, 
                                           wrap=True,from_=wordlengthlist[0],to=wordlengthlist[-1],
                                                width=3,state='readonly',font=('Arial 10 italic bold'))
        self.spboxletters.configure(command=self.update_barre_etat)
        self.spboxletters.grid(column=3, row=0, sticky='w')    
        tk.Label(frameletters, bd=0, bg='wheat',font=self.labelFont,
                              text=" Nombre d'essais :").grid(column=4,row=0,columnspan=4,sticky="w")
        self.spboxtries = tk.Spinbox(frameletters,bd=2,relief='sunken',textvariable=self.vnbessais,
                        wrap=True,from_=6,to=10,width=3,state='readonly',font=('Arial 10 italic bold'))
        self.spboxtries.configure(command=self.update_barre_etat)
        self.spboxtries.grid(column=8, row=0, sticky='e')
        # -----------------------------------------------------------------------------------------
        framegames = My_LabelFrame(self,col=9,row=0,cspan=12,bd=2,bg="tan",pad=(2,2,0,0))
        framemode = My_LabelFrame(framegames,cspan=8,bg=framegames.cget('bg'),relief='groove')
        tk.Label(framemode, bd=0, bg=framegames.cget('bg'),font=self.labelFont,
                              text=" Mode de jeu :").grid(column=0,row=0,columnspan=3,sticky="w")
        self.spboxmode = tk.Spinbox(framemode,bd=2,bg='ivory',relief='sunken',values=gamemodelist,
            command=self.__get_mode_jeu,wrap=True,width=17,state='readonly',font=('Arial 10 italic bold'))
        gamemode = self.__get_player_mode()
        while self.spboxmode.get() != gamemode: self.spboxmode.invoke('buttonup')
        self.spboxmode.grid(column=3,row=0,columnspan=5,padx=5,sticky='w')
        self.playButton = tk.Button(framegames,bg='wheat',activebackground='orange',name='!playButton',
                                    text=' Jouer ',width=12,state='active',command=self.create_GameBoard)
        self.playButton.grid(column=8,row=0,padx=5,columnspan=4,sticky="e")
        # -----------------------------------------------------------------------------------------
        self.chronometre.grid(column=21,row=0,columnspan=3,sticky="we")
        # -----------------------------------------------------------------------------------------
        frameEntry = My_LabelFrame(self,col=24,row=0,cspan=12,bg="wheat",bd=2,pad=(2,2,0,0))
        self.entryLabel = tk.Label(frameEntry,text=" Votre proposition : ",bg=self.cget('bg'),
                                                        state="disabled",disabledforeground="grey50")
        self.entryLabel.grid(column=0,row=0,columnspan=4,sticky='w')
        self.entryRequest = tk.Entry(frameEntry,bg='ivory',readonlybackground='grey90',width=22,
                state='readonly',fg="grey50",disabledforeground="grey50",textvariable=self.vrequest)
        self.entryRequest.grid(column=4,row=0,columnspan=4,sticky='e')
        self.validButton = tk.Button(frameEntry,bg='ivory',text=" Valider ",state="disabled")
        self.validButton.configure(activebackground="lightgreen",command=self.playGame)
        self.validButton.grid(column=11,row=0,columnspan=2,padx=10,sticky='nsew')
        self.validButton.__funcID = self.bind("<Return>", self.playGame)
        # -----------------------------------------------------------------------------------------
        self.abortButton = tk.Button(self,text='Quitter le jeu',bg='wheat',activebackground='red')
        self.abortButton.configure(state='normal',command=self.Quit)
        self.abortButton.grid(column=36,row=0,padx=5,columnspan=4,sticky="nsew")
        # -----------------------------------------------------------------------------------------
        # --------------- Création du tk.Canvas() pour affichage de l'image de fond ---------------
        # -----------------------------------------------------------------------------------------
        self.frame0 = My_LabelFrame(self,row=1,cspan=40,rspan=40,pad=(0,0,0,0),bd=2,relief='ridge')
        self.background = tk.Canvas(self.frame0, bd=3, relief='groove',name="!backImage",
                                                      width=self.app_size[0],height=self.app_size[1])
        self.background.grid(column=0, row=0, columnspan=40, rowspan=40, sticky='nsew')
        self.background.create_image((self.app_size[0]//2)+50, (self.app_size[1]//2)+50, 
                                        image=self.backImage, anchor="center", tags='img_background')
        # -----------------------------------------------------------------------------------------
        self.gameBoard = GameBoard(self.frame0,self.dico_Letters,col=18,row=38)   #,cspan=20,rspan=20)
        self.dico_Letters.update(self.gameBoard.create_GameBoard(self.gameBoard.bbox(), 6, 6))
        self.gameBoard.presentation_motus()     # ----- Gameboard en 6x6 pour la présentation ----- 
        # -----------------------------------------------------------------------------------------
        message = f" Info : Découvrir un MOTUS de {self.vnblettres.get()} lettres avec au maximum" \
                  f"{self.vnbessais.get()} essais\t-/- Dictionnaire '{self.dico_MOTUS.filename}' de" \
                  f"{len(self.dico_MOTUS.dico_MOTUS[str(self.vnblettres.get())])} mots.\t\tClick " \
                  f"bouton Droit de la souris ou 'Ctrl-M' pour le menu contextuel de MOTUS" 
        self.barre_Etat = Window_StateBar(self,"",1,defMessage=message,col=0,row=41,cspan=38,pady=5)
        # -----------------------------------------------------------------------------------------
        tk.Button(self,bg='lightgreen',border=1,command=self.__show_rules,text="Règles du jeu",
                    activebackground='lightblue').grid(column=38,row=41,columnspan=2,padx=2,pady=2,sticky="n")
        # -----------------------------------------------------------------------------------------        
        self.fenetre_a_propos(self.messageBox)
        self.update_idletasks()

    def update_barre_etat(self):
        message = f" Info : Découvrir un MOTUS de {self.vnblettres.get()} lettres avec au maximum" \
                  f"{self.vnbessais.get()} essais\t-/- Dictionnaire '{self.dico_MOTUS.filename}' de" \
                  f"{len(self.dico_MOTUS.dico_MOTUS[str(self.vnblettres.get())])} mots.\t\tClick " \
                  f"bouton Droit de la souris ou 'Ctrl-M' pour le menu contextuel de MOTUS" 
        self.barre_Etat.update_vltexte(message, 2)
        self.barre_Etat.get_message = message
    
    def invalid_word(self, word:str):
        self.barre_Etat.update_vltexte(f" ---> le mot que vous venez de proposer '{word}' est invalide",5)
    
    def valide_word(self, word:str) -> bool:
        return word in self.dico_MOTUS.dico_MOTUS[f"{self.vnblettres.get()}"]
    
    def select_dictionary(self):
        filename = Select_Dictionary_File(self)
        if filename:
            self.dico_MOTUS.dico_MOTUS = op.basename(filename)
            self.barre_Etat.update_vltexte("Chargement du nouveau dictionnaire",2)
            self.update_barre_etat()
    
    def __get_player_mode(self) -> str:
        """ Renvoi le libellé de la tk.Spinbox() de mode de jeu en fonction du type de joueur
            contenu dans la variable 'self.__MOTUS_Player'. 
        """
        return gamemodelist[0] if self.__MOTUS_Player == "human" else \
                        gamemodelist[1] if self.__MOTUS_Player == "computer" else gamemodelist[2]

    def __get_mode_jeu(self):
        """ Renseigne la variable 'self.__MOTUS_Player' à partir du libellé de la tk.Spinbox()
            de choix du mode de jeu. 
        """
        gamemode = self.spboxmode.get()
        self.__MOTUS_Player = "human" if gamemode == "Humain vs IA" else \
                                "computer" if gamemode == "IA vs Humain" else "fighters"
        
    def parameters(self):
        options = Parameters_Box(self, self.app_Parameters.options).go()
        if options:
            if self.app_Parameters.cfg_backup():
                self.barre_Etat.update_vltexte("Sauvegarde des paramètres OK")
                message = f" !!! Redémarrage de MOTUS nécessaire pour que la prise en compte" \
                          f" des nouveaux paramètres soit effective !!!"
                choix = My_MessageBox(self,"Redémarrage de MOTUS",message=message,action=2).go()
                # -- Redémarrage du programme sans créer de nouveau process ---
                if choix == "yes":
                    python = sys.executable             # Chemin vers l'exécutable Python
                    os.execl(python, python, *sys.argv) # Remplace le process courant par un nouveau
                # --------------------------------------------------------------
            else:
                self.barre_Etat.update_vltexte("Erreur lors le l'écriture des paramètres")
            
    def playGame(self, event=None):
        # -- Récupération du mot proposé et normalisation avec/sans accents ---
        if bool(self.app_Parameters.options.accentchar):
            word = normalize('NFC',self.vrequest.get().lower()) # - avec accents --
        else:
            word = ''.join(c for c in normalize('NFD', self.vrequest.get().lower()) if category(c) != 'Mn')
        # ---------------------------------------------------------------------
        if self.__MOTUS_Player == "human" and self.valide_word(word=word):
            # -----------------------------------------------------------------
            self.humanPlayer = Human_Player(self, self.gameBoard)
            self.__human_status = self.humanPlayer.valide_Mot(word)
            # -----------------------------------------------------------------
            if self.__human_status == "winner":
                self.gameBoard.grid_remove()
                message = f"\n{'Vous avez trouvé le mot MOTUS':100}\n{self.MOTUS_word.upper():90}\n{'Nouvelle partie ?':100}\n"
                winner_img = self.background.create_image(self.app_size[0]//2, self.app_size[1]//2, 
                                                       image=self.winnerImage, anchor="center", tags='img_winner')
                self.choose_new_game(message, winner_img)    
                self.gameBoard.grid()
            if self.__human_status == "loser":
                self.gameBoard.grid_remove()
                message = f"\n{'Vous avez perdu le mot MOTUS est :':100}\n{self.MOTUS_word.upper():90}\n{'Nouvelle partie ?':100}\n"    
                loser_img = self.background.create_image(self.app_size[0]//2, self.app_size[1]//2, 
                                                       image=self.loserImage, anchor="center", tags='img_winner')
                self.choose_new_game(message, loser_img)
                self.gameBoard.grid()
            else:
                self.chronometre.reset_chrono()
        elif self.__MOTUS_Player == "computer" and self.valide_word(word):
            self.validButton.configure(state='disabled')
            self.entryRequest.configure(state='disabled')
            # --------- IA_player joue jusqu'à ce qu'il gagne ou perde --------
            self.computerplayer = IA_Computer(self, self.gameBoard)
            self.__IA_status = self.computerplayer.valide_Mot(word)
            # -----------------------------------------------------------------
            if self.__IA_status == "winner":
                self.choose_new_game("!!! IA vainqueur !!!\n\nChanger mode de jeu ?, Ctrl-M ou click droit pour le menu contextuel",0)
            if self.__IA_status == "loser":
                self.choose_new_game("Oups, IA pas trouvé !\n\nChanger mode de jeu ?, Ctrl-M ou click droit pour le menu contextuel",0)
            self.entryRequest.configure(state='normal')            
            self.validButton.configure(state='active')
        elif self.__MOTUS_Player == "fighters":
            self.barre_Etat.update_vltexte(msg=f" /// Code en attente de développement - Mode de jeu : Joueur1 vs Joueur2")
        else:
            self.invalid_word(word=word)
                    
    def choose_new_game(self, message:str, image_ID:int):
        self.chronometre.reset_chrono(start=False)
        choix = My_MessageBox(self,"Choix de la partie MOTUS",message=message,action=0).go()
        if choix == "yes":
            self.background.delete(image_ID)
        elif My_MessageBox(self,"Quitter MOTUS","Voulez-vous quitter le jeu ?",action=1).go() == "yes":
            self.Quit()
        else:
            self.background.delete(image_ID)
        self.create_GameBoard()
            
    def create_GameBoard(self, playgame:bool=True):
        # --------------- Fonction de validation du tk.Entry() ----------------
        def _validateCmd(value:str, max:int):            
           return bool(len(value) <= int(max))
        # ---------------------------------------------------------------------
        letters, tries = self.vnblettres.get(), self.vnbessais.get()
        # ------ Mise en place de la fonction de validation du tk.Entry() -----
        _Cmd = self.entryRequest.register(_validateCmd)
        self.entryRequest.configure(validatecommand=(_Cmd,"%P",letters),validate="key")
        # ---------------------------------------------------------------------
        if self.__MOTUS_Player == "human":
            self.chronometre.start_chrono()
            self.after(10, self.update_idletasks)  # -- redemarrer la boucle --
            self.MOTUS_word = self.dico_MOTUS.dico_MOTUS_one_word(f"{letters}")
            print(f"self.MOTUS_word: {self.MOTUS_word}")            
        # ---------------------------------------------------------------------    
        self.__init_GameBoard(nb_letters=letters,nb_tries=tries)
        
    
    def __init_GameBoard(self, nb_letters:int, nb_tries:int):
        self.gameBoard.grid_remove()
        self.dico_Letters.update(self.gameBoard.create_GameBoard(self.gameBoard.bbox(),nb_letters,nb_tries))
        self.entryLabel.configure(state="normal",fg="black")
        self.entryRequest.configure(state="normal",fg="black")
        self.entryRequest.select_range(0, tk.END)
        self.spboxletters.configure(state="disabled")
        self.spboxtries.configure(state="disabled")
        self.spboxmode.configure(state="disabled")
        self.playButton.configure(state="disabled")
        self.abortButton.configure(state="normal",text="Abandonner",command=self.__abort_GameBoard)
        self.validButton.__funcID = self.bind("<Return>", self.playGame)
        self.validButton.configure(state="normal")
        self.entryRequest.focus_force()
        self.gameBoard.grid()
    
    def select_difficulty(self):
        difficulty = Difficulty_Popup(self).go()
        if difficulty:
            self.gameBoard.grid_remove()
            self.app_Parameters.options.difficulty = difficulty[1]
            self.title(f"MOTUS v3.0 - (c)AMOUROUX Bernard  Mai 2025\t{'Mode de jeu : '+self.app_Parameters.options.difficulty.upper():>140}")
            self.chronometre.change_mode(self.app_Parameters.options.difficulty)
            self.vrequest.set(f" mot MOTUS : {self.MOTUS_word.upper()}")
            self.entryRequest.configure(state="disabled",fg="grey50")
            self.validButton.configure(state="disabled")
            self.entryLabel.configure(state="disabled")
            self.abortButton.configure(state="normal")
            self.playButton.configure(state="active")
            self.spboxmode.configure(state="normal")            
            self.gameBoard.grid()
        
    def select_gamemode(self):
        self.gameBoard.grid_remove()
        self.chronometre.reset_chrono(start=False)
        self.gameBoard.presentation_motus()
        self.vrequest.set(f" mot MOTUS : {self.MOTUS_word.upper()}")
        self.entryRequest.configure(state="disabled",fg="grey50")
        self.abortButton.configure(state="disabled")
        self.validButton.configure(state="disabled")
        self.playButton.configure(state="active")
        self.spboxmode.configure(state="normal")
        select_img = self.background.create_image(self.app_size[0]//2, self.app_size[1]//2, 
                                           image=self.selectImage, anchor="center")
        self.after(3000, self.background.delete, select_img)
        self.after(1500, self.gameBoard.grid)
        self.__get_mode_jeu()
    
    def __abort_GameBoard(self):
        self.gameBoard.grid_remove()
        self.chronometre.reset_chrono(start=False)
        self.unbind("<Return>",self.validButton.__funcID)
        self.entryRequest.configure(state="disabled",fg="grey50")
        self.vrequest.set(f" mot MOTUS : {self.MOTUS_word.upper()}")
        self.spboxletters.configure(state="readonly")
        self.spboxtries.configure(state="readonly")
        self.spboxmode.configure(state="readonly")
        self.abortButton.configure(state="normal",text="Quitter le jeu",command=self.Quit)
        self.validButton.configure(state="disabled")
        self.playButton.configure(state="active")
        # --------------- Gameboard en 6x6 pour la présentation --------------- 
        self.dico_Letters.update(self.gameBoard.create_GameBoard(self.gameBoard.bbox(),6,6))
        dommage_img = self.background.create_image(self.app_size[0]//2, self.app_size[1]//2, 
                                           image=self.abortImage, anchor="center")
        self.after(2500, self.background.delete, dommage_img)
        self.after(2500, self.gameBoard.grid)
        self.gameBoard.presentation_motus()     

    def __show_rules(self):
        self.game_rules.deiconify()
        self.game_rules.lift(self)
        
    def fenetre_a_propos(self, msgbox:Win_MessageBox):
        """ Fenêtre-message à propos.
            Indique le nom de l'auteurs ainsi que la licence.
        """
        message = "MOTUS v3.0"+"\n\nCopyright (C) 2025\nBernard Amouroux\n" \
        "\nDonnées :\nDictionnaire des mots MOTUS\nhttps://www.motus.france2.fr\n\n" \
        "Sur une idée du projet JAVA 'MOTUS' de\nJan AMOUROUX \nGuillaume SOUÈDE\n\nétudiants à l'Université de Toulouse\n" \
        "Master BBS - Bio-informatique et Biologie des Systèmes\n\n" \
        "https://www.univ-tlse3.fr/decouvrir-nos-diplomes/master-mention-bio-informatique\n" \
        "\nLicense : GPL Version 3, 29 June 2007"
        msgbox.boxtitle('À propos')
        msgbox.message = message
        msgbox.lift(self)
    
    def update_idletasks(self):
        if self.app_Parameters.options.difficulty != "easy":
            if not self.chronometre.elapsed_time > 0:
                self.chronometre.pause_chrono()       
                self.__human_status = "loser"
                self.__abort_GameBoard()
                self.messageBox.boxtitle('!!! Vous avez perdu !!!')
                self.messageBox.message = f"\n  Le temps imparti est dépassé\t\n\nLe mot MOTUS était : {self.__MOTUS_word.upper()}\n\nVous avez perdu\n"
                self.messageBox.lift()
            self.after(1000, self.update_idletasks)
        return super().update_idletasks()
        
    def Quit(self):
        self.quit()
    

if __name__ == "__main__":

    if len(sys.argv) > 1:
        filename = op.basename(sys.argv[1]) if op.isfile(op.join(os.getcwd(),"data",sys.argv[1])) else None
        print(f"isfile: {op.join(os.getcwd(),'data',sys.argv[1])}\nfilename: {filename}")
    else: 
        print(f"len(sys(argv)): {len(sys.argv)}")
        filename = None
        
    app = Application(filename)
    app.mainloop()


