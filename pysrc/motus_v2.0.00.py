#!/usr/bin/python3
# -- encode utf-8 --
"""
MOTUS - Une étude Python POO , adaptation du jeu télévisé "MOTUS"
sur France Télévision en mode graphique (TKinter) et Python 3.9.5
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

DNA_GBRecords_GUI v2 (C) 2025  Bernard AMOUROUX
This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
This is free software, and you are welcome to redistribute it
under certain conditions; type `show c' for details.
"""

__author__ = "Bernard AMOUROUX"
__date__ = "$Date: 2025/05/18 07:00 $"
__copyright__ = "Copyright (c) 2025 Bernard AMOUROUX"
__license__ = "GPL 3"

import sys,os,re
import os.path as op
import tkinter as tk
import tkinter.font as tkFont

from handledico import Handle_DicoMotus
from gameboard import GameBoard
from computer import IA_Computer
from human import Human_Player

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
        # -------------- Initialisation des images de fond du jeu -------------
        self.backImage = tk.PhotoImage(master=self,
                                file=op.join(os.getcwd(),images_path,default_MOTUS_background))
        self.winnerImage = tk.PhotoImage(master=self,
                                file=op.join(os.getcwd(),images_path,"victoire.png"))
        self.loserImage = tk.PhotoImage(master=self,
                                file=op.join(os.getcwd(),images_path,"defaite.png"))
        # ---------- Initialisation des polices de caractères du jeu ----------
        self.labelFont = tkFont.Font(self,family='Courier New',size=11,weight='bold',slant='roman')
        self.menuFont = tkFont.Font(self, family='Serif', size=11, weight='normal', slant='italic')
        # ---------------------------------------------------------------------
        self.__MOTUS_word:str=""            # ---- le mot à trouver en mode 'Humain vs IA'
        self.__MOTUS_Player:str = "human"   # ---- type du joueur MOTUS, humain ou IA
        self.__dico_Letters:dict = ({})     # ---- dictionnaire de décomposition du mot en lettres
        self.player_status = "idle"         # ---- status du joueur : winner/loser/idle
        self.OK,self.IS,self.NO = 0,0,0     # ---- variable définissant le nombre et le type de lettres trouvées en mode 'human'
        self.vnbessais = tk.IntVar(value=6) # ---- nombre de mots proposables pour la partie
        self.vnblettres = tk.IntVar(value=wordlengthlist[0])    # -- Nombre de lettres du mot MOTUS
        self.vrequest = tk.StringVar(value=" Votre mot de 6 à 9 lettres ...")   # - proposition de mot en mode 'human'
        self.lmodejeu = (' Humain vs IA ',' IA vs Humain ')     # -- liste des 2 modes de jeu pour la tk.Spinbox()
        # ---------- Interception de la croix rouge en haut à droite ----------
        self.protocol('WM_DELETE_WINDOW',self.Quit)
        # ---- Taille de la fenètre du jeu fonction de la résolution écran ----
        MAX_WIDTH, MAX_HEIGHT = self.maxsize()      # --- renvoi la taille écran --
        self.app_size = min(MAX_WIDTH-100,self.backImage.width()), min(MAX_HEIGHT-150,self.backImage.height())
        self.minsize(self.backImage.width()//2, self.backImage.height()//2)
        # ---------------------------------------------------------------------
        self.title("MOTUS v1.0 (c)AMOUROUX Bernard  Mai 2025")
        [self.columnconfigure(i, weight=0) for i in range(41)]
        [self.rowconfigure(i, weight=0) for i in range(41)]
        self.resizable(False, False)
        self.configure(bg='wheat')
        # ---------------------------------------------------------------------        
        self.dico_MOTUS = Handle_DicoMotus(self, filename)
        self.messageBox = Win_MessageBox(self)
        self.cree_widgets()
        self.playGame()
    
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
        
    def cree_widgets(self):
        # -----------------------------------------------------------------------------------------
        frameletters = My_LabelFrame(self,bd=2,bg="wheat",cspan=9,pad=(2,2,0,0))
        tk.Label(frameletters, bd=0, bg='wheat',font=self.labelFont,
                              text=" Longueur du mot :").grid(column=0,row=0,columnspan=3,sticky="w")
        self.spboxletters = tk.Spinbox(frameletters,bd=2,relief='sunken',textvariable=self.vnblettres, 
                                           wrap=True,from_=wordlengthlist[0],to=wordlengthlist[-1],
                                                width=2,state='readonly',font=('Arial 10 italic bold'))
        self.spboxletters.configure(command=lambda :self.create_GameBoard(False))
        self.spboxletters.grid(column=3, row=0, sticky='w')    
        tk.Label(frameletters, bd=0, bg='wheat',font=self.labelFont,
                              text=" Nombre d'essais :").grid(column=4,row=0,columnspan=4,sticky="w")
        self.spboxtries = tk.Spinbox(frameletters,bd=2,relief='sunken',textvariable=self.vnbessais,
                        wrap=True,from_=6,to=10,width=3,state='readonly',font=('Arial 10 italic bold'))
        self.spboxtries.configure(command=lambda :self.create_GameBoard(False))
        self.spboxtries.grid(column=8, row=0, sticky='e')
        # -----------------------------------------------------------------------------------------
        framegames = My_LabelFrame(self,col=9,row=0,cspan=12,bd=2,bg="tan",pad=(2,2,0,0))
        framemode = My_LabelFrame(framegames,cspan=8,bg=framegames.cget('bg'),relief='groove')
        tk.Label(framemode, bd=0, bg=framegames.cget('bg'),font=self.labelFont,
                              text=" Mode de jeu :").grid(column=0,row=0,columnspan=3,sticky="w")
        self.spboxmode = tk.Spinbox(framemode,bd=2,bg='ivory',relief='sunken',values=self.lmodejeu,
            command=self.get_Mode_Jeu,wrap=True,width=17,state='readonly',font=('Arial 10 italic bold'))
        self.spboxmode.grid(column=3,row=0,columnspan=5,padx=5,sticky='w')
        self.playButton = tk.Button(framegames,bg='wheat',activebackground='orange',
                                text=' Jouer ',width=12,state='active',command=self.create_GameBoard)
        self.playButton.grid(column=8,row=0,padx=5,columnspan=4,sticky="e")
        # -----------------------------------------------------------------------------------------
        frameEntry = My_LabelFrame(self,col=23,row=0,cspan=13,bg="wheat",bd=2,pad=(2,2,0,0))
        self.entryLabel = tk.Label(frameEntry,text=" Votre proposition : ",bg=self.cget('bg'),
                                                        state="disabled",disabledforeground="grey50")
        self.entryLabel.grid(column=0,row=0,columnspan=4,sticky='w')
        self.entryRequest = tk.Entry(frameEntry,bg='ivory',readonlybackground='grey90',width=26,
                state='readonly',fg="grey50",disabledforeground="grey50",textvariable=self.vrequest)
        self.entryRequest.grid(column=4,row=0,columnspan=6,sticky='w')
        self.validButton = tk.Button(frameEntry,bg='ivory',text=" Valider ",state="disabled")
        self.validButton.configure(activebackground="lightgreen",command=self.playGame)
        self.validButton.grid(column=12,row=0,columnspan=2,padx=10,sticky='nsew')
        self.validButton.__funcID = self.bind("<Return>", self.playGame)
        # -----------------------------------------------------------------------------------------
        self.abortButton = tk.Button(self,text='Abandonner',bg='wheat',activebackground='red',
                                                    state='disabled', command=self.__abort_GameBoard)
        self.abortButton.grid(column=36,row=0,padx=5,columnspan=4,sticky="nsew")
        # -----------------------------------------------------------------------------------------
        # --------------- Création du tk.Canvas() pour affichage de l'image de fond ---------------
        # -----------------------------------------------------------------------------------------
        self.frame0 = My_LabelFrame(self,row=1,cspan=40,rspan=40,pad=(0,0,0,0),bd=2,relief='ridge')
        self.background = tk.Canvas(self.frame0, bd=3, relief='groove',name="!backImage",
                                                      width=self.app_size[0],height=self.app_size[1])
        self.background.grid(column=0, row=0, columnspan=40, rowspan=40, sticky='nsew')
        self.background.create_image(self.app_size[0]//2, self.app_size[1]//2, 
                                        image=self.backImage, anchor="center", tags='img_background')
        # -----------------------------------------------------------------------------------------
        self.gameBoard = GameBoard(self.frame0,self.dico_Letters,col=9,row=20,cspan=20,rspan=20)
        self.dico_Letters.update(self.gameBoard.create_GameBoard(self.gameBoard.bbox(), 6, 6))
        self.gameBoard.presentation_motus()     # ----- Gameboard en 6x6 pour la présentation ----- 
        # -----------------------------------------------------------------------------------------
        message = f" Info : Découvrir un MOTUS de {self.vnblettres.get()} lettres avec au maximum {self.vnbessais.get()} essais"
        self.barre_Etat = Window_StateBar(self,"",1,col=0,row=41,cspan=38,pady=5)
        self.barre_Etat.update_vltexte(message, 1)
        # -----------------------------------------------------------------------------------------
        tk.Button(self,bg='lightgreen',border=1,command=self.__show_rules,text="Règles du jeu",
                    activebackground='lightblue').grid(column=38,row=41,columnspan=2,padx=2,pady=2,sticky="n")
        # -----------------------------------------------------------------------------------------        
        self.fenetre_a_propos(self.messageBox)    
    
    def get_Mode_Jeu(self):
        self.__MOTUS_Player = "human" if self.spboxmode.get().strip() == "Humain vs IA" else "computer"
    
    def playGame(self, event=None):
        if self.__MOTUS_Player == "human":
            self.humanPlayer = Human_Player(self, self.gameBoard)
            self.humanPlayer.valide_Mot(event)
        else:
            human_word = self.vrequest.get().strip()
            self.computerplayer = IA_Computer(self, self.gameBoard)
            self.player_status = self.computerplayer.valide_Mot(human_word)
            if self.player_status != "idle":
                self.choose_new_game("Rejouer contre l'IA ?", 0)
            
    def choose_new_game(self, message:str, image_ID:int):
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
        self.OK, self.IS, self.NO = 0, 0, 0
        letters, tries = self.vnblettres.get(), self.vnbessais.get()
        # ------ Mise en place de la fonction de validation du tk.Entry() -----
        _Cmd = self.entryRequest.register(_validateCmd)
        self.entryRequest.configure(validatecommand=(_Cmd,"%P",letters),validate="key")
        # ---------------------------------------------------------------------
        if self.__MOTUS_Player == "human":
            self.MOTUS_word = self.dico_MOTUS.dico_MOTUS_one_word(f"{letters}")
            #print(f"self.__MOTUS_Player: {self.__MOTUS_Player} -/- self.__MOTUS_word: {self.MOTUS_word}")
        message = f" Info : Découvrir un mot MOTUS de {letters} lettres avec au maximum {tries} essais"
        self.dico_Letters.update(self.gameBoard.create_GameBoard(self.gameBoard.bbox(),letters,tries))
        if playgame: self.__init_GameBoard(nb_letters=letters,nb_tries=tries)   
        self.barre_Etat.update_vltexte(message, 1)
    
    def __init_GameBoard(self, nb_letters:int, nb_tries:int):
        self.entryLabel.configure(state="normal",fg="black")
        self.entryRequest.configure(state="normal",fg="black")
        self.entryRequest.select_range(0, tk.END)
        self.spboxletters.configure(state="disabled")
        self.spboxtries.configure(state="disabled")
        self.playButton.configure(state="disabled")
        self.abortButton.configure(state="normal")
        self.validButton.__funcID = self.bind("<Return>", self.playGame)
        self.validButton.configure(state="normal")
        self.entryRequest.focus_force()
    
    def __abort_GameBoard(self):
        self.unbind("<Return>",self.validButton.__funcID)
        self.entryLabel.configure(state="disabled",fg="grey50")
        self.entryRequest.configure(state="readonly",fg="grey50")
        self.vrequest.set(f" mot MOTUS : {self.MOTUS_word.upper()}")
        self.spboxletters.configure(state="readonly")
        self.spboxtries.configure(state="readonly")
        self.abortButton.configure(state="disabled")
        self.validButton.configure(state="disabled")
        self.playButton.configure(state="active")
    
    def __show_rules(self):
        Game_Rules(self).show_helpfile()
        
    def fenetre_a_propos(self, msgbox:Win_MessageBox):
        """ Fenêtre-message à propos.
            Indique le nom de l'auteurs ainsi que la licence.
        """
        message = "MOTUS v1.0.10"+"\n\nCopyright (C) 2025\nBernard Amouroux\n" \
        "\nDonnées :\nDictionnaire des mots MOTUS\nhttps://www.motus.france2.fr\n\n" \
        "Sur une idée du projet JAVA 'MOTUS' de\nJan AMOUROUX \nGuillaume SOUÈDE\n\nétudiants à l'Université de Toulouse\n" \
        "Master BBS - Bio-informatique et Biologie des Systèmes\n\n" \
        "https://www.univ-tlse3.fr/decouvrir-nos-diplomes/master-mention-bio-informatique\n" \
        "\nLicense : GPL Version 3, 29 June 2007"
        msgbox.boxtitle('À propos')
        msgbox.message = message
        msgbox.lift(self)
        
    def Quit(self):
        self.quit()
    

if __name__ == "__main__":

    if len(sys.argv) > 1:
        filename = sys.argv[1] if op.isfile(sys.argv[1]) else None
    else: filename = None
        
    app = Application(filename)
    app.mainloop()


