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

from gui_tools import *
from configs import *


class Application(tk.Tk):
    
    def __init__(self, filename:str):
        
        tk.Tk.__init__(self)
        
        self.backImage = tk.PhotoImage(master=self,
                                file=op.join(os.getcwd(),images_path,default_MOTUS_background))
        
        self.labelFont = tkFont.Font(self, family='Courier New', size=10, weight='bold', slant='roman')
        self.menuFont = tkFont.Font(self, family='Serif', size=11, weight='normal', slant='italic')
        
        self.__MOTUS_word:str=""
        self.__dico_Letters:dict = ({})
        self.player_status:PlayerStatus = "tries"
        self.OK,self.IS,self.NO,self.TR = 0, 0, 0, 0
        self.vnblettres = tk.IntVar(value=wordlengthlist[0])
        self.vrequest = tk.StringVar(value=" Votre mot de 6 à 9 lettres ...")
        self.vnbessais = tk.IntVar(value=6)

        self.protocol('WM_DELETE_WINDOW',self.Quit)

        MAX_WIDTH, MAX_HEIGHT = self.maxsize()
        self.app_size = min(MAX_WIDTH-100,self.backImage.width()), min(MAX_HEIGHT-150,self.backImage.height())
        self.minsize(self.backImage.width()//2, self.backImage.height()//2)
        self.title("MOTUS v1.0 (c)AMOUROUX Bernard  Mai 2025")
        [self.columnconfigure(i, weight=1) for i in range(41)]
        [self.rowconfigure(i, weight=1) for i in range(41)]
        self.resizable(False, False)
        self.configure(bg='wheat')
        
        self.dico_MOTUS = Handle_DicoMotus(self, filename=filename)
        self.messageBox = Win_MessageBox(self)
        self.cree_widgets()
        
        
    def cree_widgets(self):
        # -----------------------------------------------------------------------------------------
        tk.Label(self, bd=0, bg='wheat',font=self.labelFont,
                              text=" Longueur du mot :").grid(column=0,row=0,columnspan=3,sticky="w")
        self.spboxletters = tk.Spinbox(self,bd=3,relief='sunken',textvariable=self.vnblettres, 
                                           wrap=True,from_=wordlengthlist[0],to=wordlengthlist[-1],
                                                width=3,state='readonly',font=('Arial 10 italic bold'))
        self.spboxletters.configure(command=lambda :self.create_GameBoard(False))
        self.spboxletters.grid(column=3, row=0, sticky='w')    
        # -----------------------------------------------------------------------------------------
        tk.Label(self, bd=0, bg='wheat',font=self.labelFont,
                              text=" Nombre d'essais :").grid(column=4,row=0,columnspan=4,sticky="w")
        self.spboxtries = tk.Spinbox(self,bd=3,relief='sunken',textvariable=self.vnbessais,wrap=True,
                                     from_=6,to=10,width=3,state='readonly',font=('Arial 10 italic bold'))
        self.spboxtries.configure(command=lambda :self.create_GameBoard(False))
        self.spboxtries.grid(column=8, row=0, sticky='w')
        # -----------------------------------------------------------------------------------------
        self.playButton = tk.Button(self,text='  Jouer  ',bg='wheat',activebackground='orange',
                                                      state='active',command=self.create_GameBoard)
        self.playButton.grid(column=10,row=0,padx=5,columnspan=5,sticky="nsew")
        # -----------------------------------------------------------------------------------------
        frameEntry = My_LabelFrame(self,col=17,row=0,cspan=13,bg=self.cget('bg'),bd=2,relief="groove")
        self.entryLabel = tk.Label(frameEntry,text=" Votre proposition : ",bg=self.cget('bg'),
                                                        state="disabled",disabledforeground="grey50")
        self.entryLabel.grid(column=0,row=0,columnspan=4,sticky='w')
        self.entryRequest = tk.Entry(frameEntry,bg='ivory',readonlybackground='grey90',width=26,
                state='readonly',fg="grey50",disabledforeground="grey50",textvariable=self.vrequest)
        self.entryRequest.grid(column=4,row=0,columnspan=6,sticky='w')
        self.validButton = tk.Button(frameEntry,bg='ivory',text=" Valider ",state="disabled")
        self.validButton.configure(activebackground="lightgreen",command=self.__valide_Mot)
        self.validButton.grid(column=12,row=0,columnspan=2,padx=10,sticky='nsew')
        self.validButton.__funcID = self.bind("<Return>", self.__valide_Mot)
        # -----------------------------------------------------------------------------------------
        self.abortButton = tk.Button(self,text='Abandonner',bg='wheat',activebackground='red',
                                                    state='disabled', command=self.__abort_GameBoard)
        self.abortButton.grid(column=34,row=0,padx=5,columnspan=5,sticky="nsew")
        # -----------------------------------------------------------------------------------------
        # --------------- Création du tk.Canvas() pour affichage de l'image de fond ---------------
        frame0 = My_LabelFrame(self,row=1,cspan=40,rspan=40,pad=(0,0,0,0),bd=2,relief='ridge')
        self.background = tk.Canvas(frame0, bd=3, relief='groove',name="!backImage",
                                                      width=self.app_size[0],height=self.app_size[1])
        self.background.grid(column=0, row=0, columnspan=40, rowspan=40, sticky='nsew')
        self.background.create_image(self.app_size[0]//2, self.app_size[1]//2, 
                                        image=self.backImage, anchor="center", tags='img_background')
        # -----------------------------------------------------------------------------------------
        letters = self.vnblettres.get(); tries = self.vnbessais.get()
        self.gameBoard = GameBoard(frame0,self.__dico_Letters,letters,tries,col=9,row=30,cspan=20,rspan=10)
        self.__dico_Letters.update(self.gameBoard.create_GameBoard(self.gameBoard.bbox(),letters,tries))
        # -----------------------------------------------------------------------------------------
        message = f" Info : Découvrir un MOTUS de {self.vnblettres.get()} lettres avec au maximum {self.vnbessais.get()} essais"
        self.barre_Etat = Window_StateBar(self,"",1,col=0,row=41,cspan=40,pady=5)
        self.barre_Etat.update_vltexte(message, 1)
        self.fenetre_a_propos(self.messageBox)    
    
    def __valide_Mot(self, event=None):
        proposition = self.vrequest.get()
        if proposition != " Votre mot de 6 à 9 lettres ..." and len(proposition) == self.vnblettres.get():
            # ---- Recherche et écriture du mot dans les cases du premier mot libre ----
            word_nbr, buttons = self.__find_free_word(proposition)
            buttons = self.__draw_OK_letters(word=proposition, buttons=buttons)
            buttons = self.__draw_IS_letters(word=proposition, buttons=buttons)
            buttons = self.__draw_NO_letters(word=proposition, buttons=buttons)
            if word_nbr == self.vnbessais.get()-1 or self.OK == self.vnbessais.get():
                resultat = self.__win_loose_game()
                print(f"resultat: {resultat}")
                if resultat == "winner":
                    self.create_GameBoard()
                else:
                    self.Quit()
                 
        else:
            message = self.barre_Etat.get_message
            self.barre_Etat.update_vltexte(f" ---> le mot que vous venez de proposer '{proposition}' est invalide")
            self.barre_Etat.get_message = message
    
    
    
    def __win_loose_game(self) -> PlayerStatus:
        return "looser"if self.NO > 0 or self.IS > 0 else "winner"
    
    def __draw_NO_letters(self, word:str, buttons:list) -> PlayerStatus:
        """ Changement de la couleur de fond, le relief des lettres
            qui ne sont pas dans le mot MOTUS.
        """
        for idx,button in buttons:
            button.configure(bg='red',relief='flat',activebackground='red')
            button.flash()
        self.NO = len(buttons)
        return buttons
        
    def __draw_IS_letters(self, word:str, buttons:list):
        """ Recherche de la/des lettres/position qui sont dans le mot 
            et change la couleur de fond, le relief de ces lettres.
        """
        found:list = ([])
        for idx,button in buttons:
            if button.cget('text').lower() in self.__MOTUS_word:
                button.configure(bg='orange',relief='flat',activebackground='orange')
                found.append((idx,button))
                button.flash()
        self.IS = len(found)
        [buttons.remove(b) for b in found[::-1]]
        return buttons
    
    def __draw_OK_letters(self, word:str, buttons:list):
        """ Recherche de la/des lettres/position dans le mot qui est/sont bien placée(s)
            et change la couleur de fond, le relief de ces lettres.
        """
        found:list = ([])
        for idx,button in buttons:
            if button.cget('text').lower() == self.__MOTUS_word[idx[0] % self.vnblettres.get()]:
                button.configure(bg='lightgreen',relief='flat',activebackground='lightgreen')
                found.append((idx,button))
                button.flash()
        self.OK = len(found)
        [buttons.remove(b) for b in found[::-1]]        
        return buttons
    
    def __find_free_word(self, word:str) -> list:
        """ Recherche de la position du premier mot libre de 0 à 6...9 """
        dummy:list = ([])
        word_nbr:int = -1
        records = list(filter(lambda record:record[1]==False, self.__dico_Letters.values()))
        if records:
            for l in range(self.gameBoard.nb_Letters):
                btn_ID, tries, letter = records[l][0]
                self.__dico_Letters[tries,l] = ((btn_ID,tries,word[l]), True, records[l][2])
                self.__dico_Letters[tries,l][2].configure(text=word[l].upper())
                dummy.append((records[l][0],records[l][2]))
            word_nbr = records[0][0][1]
            self.vrequest.set("")
        self.TR += 1
        return word_nbr, dummy
        
    def create_GameBoard(self, playgame:bool=True):
        # --------------- Fonction de validation du tk.Entry() ----------------
        def _validateCmd(value:str, max:int):
            return bool(len(value) <= int(max))
        # ---------------------------------------------------------------------
        self.OK,self.IS,self.NO,self.TR = 0, 0, 0, 0 
        letters, tries = self.vnblettres.get(), self.vnbessais.get()
        # ------ Mise en place de la fonction de validation du tk.Entry() -----
        _Cmd = self.entryRequest.register(_validateCmd)
        self.entryRequest.configure(validatecommand=(_Cmd,"%P",letters),validate="key")
        # ---------------------------------------------------------------------
        self.__MOTUS_word = self.dico_MOTUS.dico_MOTUS_one_world(f"{letters}")
        message = f" Info : Découvrir un mot MOTUS de {letters} lettres avec au maximum {tries} essais"
        self.__dico_Letters.update(self.gameBoard.create_GameBoard(self.gameBoard.bbox(),letters,tries))
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
        self.validButton.__funcID = self.bind("<Return>", self.__valide_Mot)
        self.validButton.configure(state="normal")
        self.entryRequest.focus_force()
    
    def __abort_GameBoard(self):
        self.unbind("<Return>",self.validButton.__funcID)
        self.entryLabel.configure(state="disabled",fg="grey50")
        self.entryRequest.configure(state="readonly",fg="grey50")
        self.vrequest.set(f" mot MOTUS : {self.__MOTUS_word.upper()}")
        self.spboxletters.configure(state="readonly")
        self.spboxtries.configure(state="readonly")
        self.abortButton.configure(state="disabled")
        self.validButton.configure(state="disabled")
        self.playButton.configure(state="active")
        
    def fenetre_a_propos(self, msgbox:Win_MessageBox):
        """ Fenêtre-message à propos.
            Indique le nom de l'auteurs ainsi que la licence.
        """
        message = "MOTUS v0.0.10"+"\n\nCopyright (C) 2025\nBernard Amouroux\n" \
        "\nDonnées :\nDictionnaire des mots MOTUS\nhttps://www.motus.france2.fr\n\n" \
        "Sur une idée du projet 'MOTUS' de \nl'Université de Toulouse - Master BBS\nBio-informatique et Biologie des Systèmes\n-\n"\
        "https://www.univ-tlse3.fr/decouvrir-nos-diplomes/master-mention-bio-informatique\n" \
        "\nLicense : GPL Version 3, 29 June 2007"
        msgbox.boxtitle('À propos')
        msgbox.message = message
        msgbox.lift(self)
        
    def Quit(self):
        self.destroy()
    

if __name__ == "__main__":

    if len(sys.argv) > 1:
        filename = sys.argv[1] if op.isfile(sys.argv[1]) else None
    else: filename = None
        
    app = Application(filename)
    app.mainloop()


