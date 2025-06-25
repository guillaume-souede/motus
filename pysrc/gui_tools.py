# coding: utf-8
"""
Bibliotheque 'GUI_Tools'. Quelques surcharges de classes tKinter 
pour le jeu MOTUS inspiré du jeu télévisé diffusé sur France2
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

'gui_tools' library for MOTUS v4.0 (C) 2025  Bernard AMOUROUX
This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
This is free software, and you are welcome to redistribute it
under certain conditions; type `show c' for details.
"""

__author__ = "Bernard AMOUROUX"
__date__ = "$Date: 2025/05/18 07:00 $"
__copyright__ = "Copyright (c) 2025 Bernard AMOUROUX"
__license__ = "GPL 3"

import os.path as op
import tkinter as tk
import tkinter.font as tkFont
import tkinter.filedialog as tkFileDialog

from time import time
from os import getcwd
from configs import *


class Chronometre(tk.Frame):
    
    def __init__(self, master, mode:Gamehardness, *args, **kwargs):
        
        self.master = master
        self.__dict_modes:dict = {"easy":0,"normal":120,"hardu":60,"terrible":30,'infaisable':10} 
        # ---------------------------------------------------------------------
        tab_options:dict = {'bg':'grey90' if mode == "easy" else 'ivory', 'bd':3, 
                                     'relief':'sunken' if mode == "easy" else 'ridge'}
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        # ---------------------------------------------------------------------
        self.__vchrono = tk.StringVar(value="00:00")     # temps en secondes
        self.__rebour:bool = mode != "easy"
        self.__max_time = self.__dict_modes.get(mode, 'normal')
        self.__time = self.__max_time
        self.__mode = mode
        self.actif = False
        # ---------------------------------------------------------------------
        self.create_widget()

    @property
    def elapsed_time(self) -> int:
        return self.__time

    def create_widget(self):
        lbl_font = tkFont.Font(family='Sans Serif',size=16,weight='normal',slant='roman')
        self.chrono_lbl = tk.Label(self,bg=self.cget('bg'),font=lbl_font,textvariable=self.__vchrono,border=0)
        self.chrono_lbl.configure(fg="black" if self.__rebour else "grey75")
        self.chrono_lbl.grid(column=0,row=0,sticky="nsew")
        self.update_idle()

    def __format_time(self):
        minutes = self.__time // 60
        secondes = self.__time % 60
        return f" {minutes:02} : {secondes:02} "

    def update_idle(self):
        if self.actif and not self.__rebour:
            self.__time += 1 
        if self.actif and self.__rebour and self.__time > 0:
            self.__time -= 1
        self.__vchrono.set(self.__format_time())
        self.after(1000, self.update_idle)

    def start_chrono(self):
        self.actif = True

    def pause_chrono(self):
        self.actif = False

    def change_mode(self, newmode:Gamehardness):
        self.__max_time = self.__dict_modes.get(newmode, 'normal')
        self.__rebour:bool = newmode != "easy"
        self.__time = self.__max_time
        self.configure(relief='ridge' if not self.__rebour else 'sunken')
        self.chrono_lbl.configure(bg=self.cget('bg'), fg="black" if self.__rebour else "grey75")
        self.reset_chrono(start=False)
        
    def reset_chrono(self, start:bool=True):
        self.actif = start
        self.__vchrono.set(" 00 : 00 ")
        self.__time = self.__max_time
    

class My_LabelFrame(tk.LabelFrame):

    def __init__(self,master,col=0,row=0,cspan=1,rspan=1,pad=(0,0,0,0),sticky='nsew', *args, **kwargs):

        tab_options:dict = {'bg':'ivory', 'bd':3, 'relief':'groove', 'labelanchor':'n'}
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)

        tk.LabelFrame.__init__(self,master,*args,**kwargs)        

        self.grid(column=col, row=row, columnspan=cspan, rowspan=rspan,
                                  padx=pad[0], pady=pad[1], ipadx=pad[2], ipady=pad[3], sticky=sticky)
        [self.grid_columnconfigure(r, weight=1) for r in range(cspan)]        
        [self.grid_rowconfigure(r, weight=1) for r in range(rspan)]

    def name(self):
        return f"{self.master}."+self._name
        
    
class Window_StateBar(tk.Frame):
    
    def __init__(self, master, message, waitime, col=0, row=0, cspan=1, sticky="nsew",
                        defMessage:str=' Info : ', defTime:int=10,*args,**kwargs):
        
        self.__master = master
        self.__message = message
        self.__waitnbr:int = None
        self.__defaultTime = defTime
        self.__defaultMsg = defMessage
        self.__vl_texte = tk.StringVar()
        self.__wait = waitime

        tab_options:dict = {'bd':1, 'bg':'tan2', 'relief':'groove'}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        
        self.grid(column=col,row=row,columnspan=cspan,padx=2,pady=2,sticky=sticky)
        tk.Label(self,bd=0,bg=self.cget('bg'),anchor="sw",height=1,
                    font="Courier\ New 10 bold italic",textvariable=self.__vl_texte).grid()
        self.update_vltexte(defMessage if not message else message, waitime)

    @property
    def get_message(self) -> str:
        return self.__vl_texte.get()
    @get_message.setter
    def get_message(self, default_msg:str):
        self.__defaultMsg = default_msg
        
    def __raz_vltexte__(self):
        self.__vl_texte.set(self.__defaultMsg)
        self.__waitnbr = None
    
    def update_vltexte(self, msg:str, wait=10):
        if self.__waitnbr != None:
            self.after_cancel(self.__waitnbr)
        if wait == 0:
            return
        elif wait > 1:
            self.__waitnbr = self.after(self.__defaultTime if wait==None else (wait*1000) , self.__raz_vltexte__)
        self.__vl_texte.set(msg)


class Win_MessageBox(tk.Toplevel):
    
    def __init__(self, master:tk.Tk, message:str=None, *args, **kwargs):
        
        self.__master = master
        self.__name__ = '!win_messagebox'
        self.__vmessage = tk.StringVar(value=message)
        
        tab_options:dict = {'bd':3, 'bg':'wheat', 'relief':'ridge', 'pady':5}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        
        self.protocol("WM_DELETE_WINDOW", self.Quit)
        self.resizable(False, False)
        
        tk.Message(self,bg='wheat',width=500,aspect=100,justify=tk.CENTER,font=("Courier New",14,"bold","italic"),
                                           textvariable=self.__vmessage).grid(padx=10,pady=10,sticky="nsew")
        tk.Button(self,width = 8,bg='tan',text='Ok',font=('TkDefaultFont 12 bold italic'),command=self.Quit).grid()
        self.bind('<Return>', self.Quit)
        self.withdraw()
    
    @property
    def message(self)->str:
        return self.__vmessage.get()
    @message.setter
    def message(self, message):
        self.__vmessage.set(message)
        if not self.winfo_ismapped():
            self.deiconify()
        self.update_idletasks()
        
    def boxtitle(self, title:str):
        self.title(title) 
    
    def Quit(self, event=None):
        self.boxtitle(" Message ")
        self.withdraw()    
   

class My_MessageBox(tk.Toplevel):
    
    def __init__(self, master:tk.Tk, title:str, message:str=None, action:int=0, *args, **kwargs):
        
        self.__master = master
        self.__vtitle = tk.StringVar(value=title)
        self.__vmessage = tk.StringVar(value=message)
        tab_action = [(" Rejouer "," Quitter "),("Oui","Non"),(" Redémarrer "," Plus tard ")]
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':3,'bg':'wheat','relief':'ridge','name':"!my_MessageBox"}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        # ---------------------------------------------------------------------        
        self.protocol("WM_DELETE_WINDOW", self.choose_cancel)
        msg_font = ('Courier\ New 16 bold italic')
        btn_font = ('Courier\ New 14 bold italic')
        self.bind("<Escape>", self.no_command)
        self.bind("<Return>", self.ok_command)
        self.title(self.__vtitle.get())
        self.resizable(False, False)
        # ---------------------------------------------------------------------
        self.withdraw()
        title1,title2 = tab_action[action][0], tab_action[action][1]
        tk.Message(self,bg='wheat',width=600,aspect=100,justify=tk.CENTER,font=msg_font,
                                               textvariable=self.__vmessage).grid(padx=10,pady=10,
                                                      column=0,row=0,columnspan=6,rowspan=4,sticky="nsew")
        self.playButton = tk.Button(self,text=title1,width=12,font=btn_font,state="active",
                                            activebackground="lightgreen",command=self.ok_command,padx=10)
        self.playButton.grid(column=1,row=4,pady=10,sticky="nw")
        self.quitButton = tk.Button(self,text=title2,width=12,font=btn_font,
                                            activebackground="tan",command=self.no_command,padx=10)
        self.quitButton.grid(column=3,row=4,pady=10,sticky="ne")
        # ---------------------------------------------------------------------
        self.deiconify()
        self.focus_set()

    def valide_ok_command(self, event):
        print(f"event: {event}")

    def go(self):
        """ Methode qui permet de garder le focus sur la fenetre de choix
            de l'huile qui lors du choix renvoi le nom de l'huile choisie
            et ferme la fenetre Toplevel.
        """
        self.lift(self.__master)        # mise au premier plan de la Toplevel
        self.wait_visibility()
        self.grab_set() 
        self.how = None                 # Nom de la procédure exécutée en sortie
        self.mainloop()                 # Sortie de la Boucle principale par "self.quit(how)"
        self.destroy()                  # Fermeture de la fenetre Toplevel
        return self.how

    def choose_ok(self) -> str:
        return "yes"

    def choose_nok(self) -> str:
        return "no"
    
    def choose_cancel(self):
        self.Quit(None)
        
    def ok_command(self, event=None):
        self.Quit(self.choose_ok())
        
    def no_command(self, event=None):
        self.Quit(self.choose_nok())
        
    def Quit(self, how=None):
        """ Sortie de la boucle principale et non fermeture de la fenetre
            Exécution de la methode "how" qui permet de récupérer la
            donnée voulue en sotie en fin de méthode "go()"
        """
        self.how = how
        self.quit()                     # Exit mainloop()
        

class Game_Rules(tk.Toplevel):
        
    def __init__(self, master, parameters:"Saveload_CFG"=None, *args, **kwargs):
         
        self.__master = master
        # ---------------------------------------------------------------------
        if isinstance(parameters, Saveload_CFG):
            self.__filename, self.__help_path = parameters.options.helpfilename, parameters.options.dicopath
        else:
            self.__filename = parameters if isinstance(parameters,str) else default_help_filename
            self.__help_path = dico_path 
        # ---------------------------------------------------------------------
        self.__filename =  op.join(getcwd(),self.__help_path,self.__filename)
        self.__vtitle = tk.StringVar(value="Aide de MOTUS v4.0 (c)AMOUROUX Bernard 05/2025")
        self.__dico_lines:dict[int:str] = ({})
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':3, 'bg':'ivory', 'relief':'ridge', 'name':"!my_gameRules"}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        # ---------------------------------------------------------------------        
        self.protocol("WM_DELETE_WINDOW", self.Quit)
        # ---------------------------------------------------------------------        
        self.txt_font = ('Courier\ New 16 bold italic')
        self.bind_all("<Escape>", self.Quit)
        self.minsize(master.app_size[0]//3, master.app_size[1]//3)
        self.columnconfigure(index=0, weight=1)
        self.rowconfigure(index=0, weight=1)
        self.title(self.__vtitle.get())
        # ---------------------------------------------------------------------
        self.__text_help = tk.Text(self,bg='ivory',bd=0,font=self.txt_font,wrap="word",relief="flat",width=90)
        self.__load_helpfile()
        self.create_widgets()
        self.withdraw()
        
    def create_widgets(self):
        # ------------------ Tags à placer dans le tk.Text() ------------------
        text_tags = [(" Carré Rouge  ","ok",COLOR_OK),
                     (" Carré Bleu   ","is",COLOR_IS),
                     (" Carré Jaune  ","no",COLOR_NO),       ]
        # ---------------------------------------------------------------------
        versb = tk.Scrollbar(self, orient=tk.VERTICAL)
        versb.grid(column=1,row=0,sticky='nse')
        self.__text_help.grid(column=0,row=0,padx=10,pady=10,sticky="nsew")
        self.__text_help['yscrollcommand'] = versb.set   
        versb['command'] = self.__text_help.yview
        # ------------ Insertion du texte dans le widget tk.Text() ------------        
        [self.__text_help.insert(tk.END, f"{line}\n") for line in self.__dico_lines.values()]
        # ---------------------------------------------------------------------
        self.__look_for_tags(self.__text_help.get("1.0", tk.END),text_tags)
        self.__text_help.configure(state="disabled")
    
    def __load_helpfile(self):
        """ Charge le fichier d'aide au format texte donné au constructeur de
            la classe et à défaut le fichier de la librairie 'configs.py'
            contenu dans la constante 'default_help_filename'.
        """
        if not op.isfile(self.__filename):
            raise FileNotFoundError(f"Bad file name: {self.__filename}")
        with open(file=self.__filename, mode='rt', encoding="utf-8") as helpfile:
            for idx,line in enumerate(helpfile.readlines()):
                self.__dico_lines[idx] = line.rstrip()
    
    def __look_for_tags(self, help_text:str,text_tags:list):
        """ Methode qui ajoute des tags au texte. La recherche du pattern se fait
            avec la méthode Text().search() intégrée à l'interpréteur Tcl(). 
            parametres:
                'help_text': texte que l'on veut 'tager'
                'text_tags': tuple de la forme -> (pattern, tagname) 
        """
        count = tk.IntVar()
        for pattern,tagname,color in text_tags:
            found = self.__text_help.search(pattern,"1.0",tk.END,count=count,regexp=True)
            if found:
                self.__text_help.tag_configure(tagname, background=color,
                                        border=3,spacing1=5,spacing3=5,relief='raised')
                self.__text_help.tag_add(tagname,f"{found}", f"{found}+{count.get()}c")
    
    def get_Parent(self)->tk.Tk:
        return self.__master
    
    def get_TextWidget(self)->tk.Text:
        return self.__text_help
    
    def Quit(self, event=None):
        self.withdraw()


class Motus_PopupMenu(tk.Menu):
    """ Classe Menu popup paramétrable.
        Les parametres du contructeur sont:
            'master': widget appelant dont le parent est un tk.Toplevel()
        Les 2 paramètres suivants sont optionnels et non nécessaire pour 'Copier/Coller Couper' !
            'commandsList': tuple de la forme (label_cmd:str, accel_cmd:str,commande:list[callable])
            'nosel' : list[int] liste des indices des rubrique dont l'état sera 'disabled'
    """
    def __init__(self, master:tk.Tk, commandsList:tuple=None, nosel:list=None):
        
        self.__master = master
        self.__commandList = commandsList
        self.__NoSel = nosel
        
        super().__init__(master,tearoff=0,font=('Arial 12 bold italic'),postcommand=lambda :self.nomenupopup(self.__NoSel))
            
    def show_Menu_Popup(self, event:tk.Event):
        # --------------- Recherche de l'état du bouton 'Jouer' ---------------
        button = self.nametowidget(self.__master.__dict__.get('playButton',"."))
        if isinstance(button, tk.Button): 
            state = bool(button.cget('state') == "disabled")
            if state and 4 in self.__NoSel:
                self.__NoSel.remove(4) 
            elif not state and not 4 in self.__NoSel:
                self.__NoSel.append(4)
        # ---------------------------------------------------------------------
        self.delete(0, 'end')   # -- reset de la liste des commandes ajoutées -
        self.add_command(label=" MOTUS popup menu",accelerator =" Ctrl-M ",
                                                        background='orange',activebackground='orange')
        self.configure(background="ivory", activebackground='tan',borderwidth=2,relief="solid")
        self.add_separator()
        self.add_Popup_Commands(commands=self.__commandList)
        # ---------------------------------------------------------------------
        try:
            self.tk_popup(event.x_root, event.y_root)
        except Exception as e:
            print(f"Erreur interne : {e}")
            self.grab_release()
             
    def nomenupopup(self,nosel:list):                
        [self.entryconfigure(i, state = 'disabled') for i in nosel]

    def add_Popup_Commands(self, commands:tuple):
        for command in commands:
            if command[0] == "separator":
                self.add_separator()
            else:
                self.add_command(label=command[0],accelerator=command[1],command=command[2])   
        
        
class Difficulty_Popup(tk.Toplevel):
    """ Affichage d'une fenetre popup toujours au premier plan sans boutons système
        Se ferme après avoir choisi un élément dans la liste.
    """
    def __init__(self, master, *args, **kwargs):
        # ---------------------------------------------------------------------
        self.__master = master
        self.__difficulty = master.app_Parameters.options.difficulty
        self.__difficulty_list:list[Gamehardness] = gamehardlist
        self.vgamedif = tk.StringVar(value=self.__difficulty_list)
        self.lst_font = tk.font.Font(family='Courier New',size=10,weight='bold',slant='italic')    
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':2, 'bg':'orange', 'relief':'flat'}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, name="!difficulty_popup", *args, **kwargs)
        # ---------------------------------------------------------------------        
        self.overrideredirect(1)                                  # - Aucun bouton systeme sur la fenetre   
        self.bind_class(self,'<Button2-Motion>',self.motion)      # - Bouton droit pour déplacer la fenètre popup
        self.bind_class(self,"<Escape>", self.cancel_command)     # - pour quitter la popupList pas 'Esc'
        self.event_add("<<Select_Item>>",'<Double-Button-1>','<KP_Enter>','<Return>')
        self.bind_class(self,"<<Select_Item>>",self.select_item)  # - click gauche/Enter pour selectionner le codon 
        # ---------------------------------------------------------------------
        frame1 = My_LabelFrame(self, bd=0, bg='ivory', relief='flat')
        self.lst = tk.Listbox(frame1,bg='ivory',bd=0,listvariable=self.vgamedif,activestyle="dotbox",
                                       font=self.lst_font,relief='flat',width=12,height=6,state="normal")
        self.lst.activate(self.lst.get(0, tk.END).index(self.__difficulty))
        self.lst.select_set('active')
        self.lst.grid(sticky='nsew')
        self.lst.update()    
        # ---------------------------------------------------------------------
        pos = self.geometry()
        mousexy = master.winfo_pointerxy() # - Récupère la position de la souris
        self.lst_pos = f"{pos[:pos.find('+')]}"
        self.geometry(f"{self.lst_pos}+{mousexy[0]}+{mousexy[1]-100}")
        self.lst.focus_set()     
        self.update()
    
    def getSelectItem(self):
        """ Renvoi l'index et le libellé de la premiere sélection de la listbox """
        idx = self.lst.index('active') if not self.lst.curselection() else self.lst.curselection()[0]
        return int(idx), self.lst.get(idx)
    
    def motion(self, event):
        mousexy = self.__master.winfo_pointerxy()
        self.geometry(f"{self.lst_pos}+{mousexy[0]}+{mousexy[1]}")

    def go(self):
        """ Methode qui permet de garder le focus sur la fenetre de choix
            de l'huile qui lors du choix renvoi le nom de l'huile choisie
            et ferme la fenetre Toplevel.
        """
        self.lift(self.__master)        # mise au premier plan de la Toplevel
        self.grab_set() 
        self.how = self.getSelectItem   # Nom de la procédure exécutée en sortie
        self.mainloop()                 # Sortie de la Boucle principale par "self.quit(how)"
        self.destroy()                  # Fermeture de la fenetre Toplevel
        return self.how

    def select_item(self, event):
        self.ok_command()
        
    def ok_command(self):
        self.Quit(self.getSelectItem())

    def cancel_command(self, event):
        """ Sortie de la Toplevel avec "None" en retour """
        self.Quit(None)
        
    def Quit(self, how=None):
        """ Sortie de la boucle principale et non fermeture de la fenetre
            Exécution de la methode "how" qui permet de récupérer la
            donnée voulue en sotie en fin de méthode "go()"
        """
        #self.wm_attributes("-topmost", 0)                     # - Fenetre popup NON au premier plan
        self.how = how
        self.quit()              # Exit mainloop()
        
        
class Parameters_Box(tk.Toplevel):
    
    def __init__(self, master, parameters:App_Options, *args, **kwargs):
         
        self.__master = master
        self.__parameters = parameters
        # ---------------------------------------------------------------------
        self.vfullscreen = tk.IntVar(value=int(parameters.fullscreen))
        self.vparamfile =  op.join(getcwd(),parameters.dicopath,parameters.paramfilename)
        self.vdicofile = tk.StringVar(value=parameters.dicofilename)
        self.vhelpfile = tk.StringVar(value=parameters.helpfilename)
        self.vbackfile = tk.StringVar(value=parameters.backfilename)
        self.vdifficulty = tk.StringVar(value=parameters.difficulty)
        self.vlettersound = tk.IntVar(value=parameters.lettersound)
        self.vimagepath = tk.StringVar(value=parameters.imagepath)
        self.vaccentchar = tk.IntVar(value=parameters.accentchar)
        self.vgamemode = tk.StringVar(value=parameters.gamemode)
        self.vdatapath = tk.StringVar(value=parameters.dicopath)
        self.vmusicgame = tk.IntVar(value=parameters.musicgame)
        self.vsoundgame = tk.IntVar(value=parameters.soundgame)
        self.vnblettres = tk.IntVar(value=wordlengthlist[0]) 
        self.vnbtries = tk.IntVar(value=wordlengthlist[0])
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':3,'bg':'ivory','relief':'ridge','name':"!my_appParameters"}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        # ---------------------------------------------------------------------        
        self.protocol("WM_DELETE_WINDOW", self.no_command)
        # ---------------------------------------------------------------------        
        self.txt_font = ('Courier\ New 12 bold italic')
        self.btn_font = ('Arial 12 bold roman')
        self.lbl_font = ('Serif 12 normal italic')
        self.spb_font = ('Serif 11 normal italic')
        # ---------------------------------------------------------------------        
        self.minsize(master.app_size[0]//3, master.app_size[1]//3)
        [self.columnconfigure(index=i, weight=1) for i in range(20)]
        self.rowconfigure(index=0, weight=1)
        # ---------------------------------------------------------------------        
        title = "Paramétrage de MOTUS v4.0 (c)AMOUROUX Bernard 05/2025"
        self.bind("<Return>", self.ok_command)
        self.bind("<Escape>", self.no_command)
        self.title(title)
        # ---------------------------------------------------------------------
        self.create_widgets()
    
    def create_widgets(self):
        # ---------------------------------------------------------------------
        frame0 = My_LabelFrame(self,0,0,cspan=20,rspan=21,bg='wheat',bd=2,relief="groove")
        tk.Label(frame0,bg='tan',bd=0,font=self.lbl_font,justify="center",anchor="center",width=32,
                    text=f"\tFichier de configuration :").grid(row=0,columnspan=12,sticky='nsew')
        tk.Label(frame0,bg='tan',bd=0,font=self.txt_font,text=f"\t{self.__parameters.paramfilename}",
                   width=38,justify="center",anchor="w").grid(column=12,row=0,columnspan=8,sticky='nsew')
        # ---------------------------------------------------------------------
        frame1 = My_LabelFrame(frame0,0,1,cspan=20,rspan=9,bg='wheat',bd=2,relief="ridge",pad=(2,2,0,0))
        tk.Label(frame1,bg=frame1.cget('bg'),bd=0,text="  Fichier dictionnaire  :",anchor="sw",
                           width=22,font=self.lbl_font).grid(column=0,row=0,columnspan=5,sticky='nsew')
        tk.Entry(frame1,bg='ivory',width=40,textvariable=self.vdicofile,state="readonly").grid(row=0,
                                                           ipady=3,column=5,columnspan=10,sticky='sew')
        tk.Button(frame1,bg='orange',text=' Choisir ',width=12,activebackground="lightblue",
                     command=self.sel_Dictfile,).grid(row=0,column=15,columnspan=4,padx=10,sticky="se")
        tk.Label(frame1,bg=frame1.cget('bg'),bd=0,text="  Fichier d'aide MOTUS  :",anchor="sw",
                           width=22,font=self.lbl_font).grid(column=0,row=1,columnspan=5,sticky='nsew')
        tk.Entry(frame1,bg='ivory',width=40,textvariable=self.vhelpfile,state="readonly").grid(row=1,
                                                           ipady=3,column=5,columnspan=10,sticky='sew')
        tk.Button(frame1,bg='orange',text=' Choisir ',width=12,activebackground="lightblue",
                      command=self.sel_helpfile).grid(row=1,column=15,columnspan=4,padx=10,sticky="se")
        tk.Label(frame1,bg=frame1.cget('bg'),bd=0,text="  Image de fond d'écran :",anchor="sw",
                           width=22,font=self.lbl_font).grid(column=0,row=2,columnspan=5,sticky='nsew')
        tk.Entry(frame1,bg='ivory',width=40,textvariable=self.vbackfile,state="readonly").grid(row=2,
                                                           ipady=3,column=5,columnspan=10,sticky='sew')
        tk.Button(frame1,bg='orange',text=' Choisir ',width=12,activebackground="lightblue",
                      command=self.sel_backfile).grid(row=2,column=15,columnspan=4,padx=10,sticky="se")
        # ---------------------------------------------------------------------
        tk.Label(frame1,bg=frame1.cget('bg'),bd=0,anchor="s",width=40,
                    text=f"{'Dossiers des dictionnaires, paramètres et images :'}",
                          font=self.lbl_font).grid(column=0,row=3,rowspan=3,columnspan=20,sticky='nsew')
        tk.Label(frame1,bg=frame1.cget('bg'),bd=0,text="  Dossier des données :",anchor="sw",
                            width=22,font=self.lbl_font).grid(column=0,row=6,columnspan=5,sticky='nsew')
        tk.Entry(frame1,bg='ivory',width=40,textvariable=self.vdatapath,state="readonly").grid(row=6,
                                                            ipady=3,column=5,columnspan=10,sticky='sew')
        tk.Button(frame1,bg='orange',text=' Choisir ',width=12,activebackground="lightblue",
                       command=self.sel_datapath).grid(row=6,column=15,columnspan=4,padx=10,sticky="se")
        tk.Label(frame1,bg=frame1.cget('bg'),bd=0,text="  Dossier des images  :",anchor="sw",
                            width=22,font=self.lbl_font).grid(column=0,row=7,columnspan=5,sticky='nsew')
        tk.Entry(frame1,bg='ivory',width=40,textvariable=self.vimagepath,state="readonly").grid(row=7,
                                                            ipady=3,column=5,columnspan=10,sticky='sew')
        tk.Button(frame1,bg='orange',text=' Choisir ',width=12,activebackground="lightblue",
                      command=self.sel_imagepath).grid(row=7,column=15,columnspan=4,padx=10,sticky="se")
        # ---------------------------------------------------------------------
        chkboxlbl = My_LabelFrame(frame1,0,8,cspan=20,rspan=2,bg=frame0.cget('bg'),
                                      relief="ridge",text=' Paramètres divers',pad=(0,0,0,0))
        tk.Checkbutton(chkboxlbl,bg=chkboxlbl.cget('bg'),variable=self.vaccentchar,
                                indicatoron=1,font=self.spb_font,text=" : mots accentués",
                                        anchor='w',).grid(column=0,row=0,columnspan=3,sticky='nsew')
        tk.Checkbutton(chkboxlbl,bg=chkboxlbl.cget('bg'),variable=self.vfullscreen,
                                indicatoron=1,font=self.spb_font,text=" : mode plein écran/fenêtré",
                                        width=26,anchor='center',).grid(column=3,row=0,columnspan=8,sticky='nsew') 
        tk.Label(chkboxlbl,bg=chkboxlbl.cget('bg'),text=f"Nombre d'essais : ",anchor='e',
                                font=self.spb_font).grid(column=11,columnspan=8,row=0,padx=10,sticky="w")
        tk.Spinbox(chkboxlbl,bd=2,relief='sunken',textvariable=self.vnbtries,wrap=True,
                                      from_=wordlengthlist[0],to=11,state='readonly',width=2,
                                              font=self.txt_font).grid(column=19,row=0,sticky='e')
        tk.Label(chkboxlbl,bg=chkboxlbl.cget('bg'),text=f" Mode de jeu : ",
                                font=self.spb_font).grid(column=0,columnspan=2,row=1,sticky="nsew")
        gamemode = gamemodelist[0] if self.__parameters.gamemode=="human" else \
                        gamemodelist[1] if self.__parameters.gamemode=="computer" else gamemodelist[2]
        self.spbgmode = tk.Spinbox(chkboxlbl,bg='ivory',activebackground='ivory',state="readonly",
                                textvariable=self.vgamemode,values=gamemodelist,wrap=True,width=15)
        while self.spbgmode.get() != gamemode: self.spbgmode.invoke('buttonup')
        self.spbgmode.grid(column=2,row=1,columnspan=2,sticky="ew")
        tk.Label(chkboxlbl,bg=chkboxlbl.cget('bg'),text=f"    Difficulté : ", anchor="w",
                                font=self.spb_font).grid(column=4,columnspan=1,row=1,sticky="nsew")
        self.spbdifficulty = tk.Spinbox(chkboxlbl,bg='ivory',activebackground='ivory',state="readonly",
                                textvariable=self.vdifficulty,values=gamehardlist,wrap=True,width=10)
        while self.spbdifficulty.get() != self.__parameters.difficulty: self.spbdifficulty.invoke('buttonup')
        self.spbdifficulty.grid(column=6,row=1,columnspan=2,sticky="ew")
        tk.Label(chkboxlbl,bg=chkboxlbl.cget('bg'),text=f"Longueur du mot : ",anchor="e",
                    font=self.spb_font).grid(column=17,columnspan=2,row=1,padx=10,sticky="nsew")
        tk.Spinbox(chkboxlbl,bd=2,relief='sunken',textvariable=self.vnblettres,wrap=True,
                              from_=wordlengthlist[0],to=wordlengthlist[-1],state='readonly',
                                    width=2,font=self.txt_font).grid(column=19,row=1,sticky='w')    
        # ---------------------------------------------------------------------
        musicframe = My_LabelFrame(frame1,0,10,cspan=20,rspan=2,bg=frame0.cget('bg'),
                                      relief="ridge",text=' Paramètres effets sonores ',pad=(0,0,0,0))  
        tk.Checkbutton(musicframe,bg=musicframe.cget('bg'),variable=self.vmusicgame,
                                indicatoron=1,font=self.spb_font,text=" : lire le générique",
                                        anchor='w',).grid(column=0,row=0,columnspan=3,sticky='nsew')
        tk.Checkbutton(musicframe,bg=musicframe.cget('bg'),variable=self.vsoundgame,
                                indicatoron=1,font=self.spb_font,text=" : gingles gagné/perdu ",
                                        width=26,anchor='center',).grid(column=3,row=0,columnspan=8,sticky='nsw') 
        tk.Checkbutton(musicframe,bg=musicframe.cget('bg'),variable=self.vlettersound,
                                indicatoron=1,font=self.spb_font,text=" : tonalités lettres Motus ",
                                        width=26,anchor='center',).grid(column=11,row=0,columnspan=8,sticky='nse') 
        # ---------------------------------------------------------------------        
        tk.Button(frame0,bg='tan',bd=3,activebackground="lightgreen",state="active",width=12,font=self.btn_font,
                      text="Valider",command=self.ok_command).grid(column=2,row=21,columnspan=3,pady=5,sticky="w")
        tk.Button(frame0,bg='tan',activebackground="red",text="Annuler",font=self.btn_font,bd=3,width=12,
                               command=self.no_command).grid(column=15,row=21,columnspan=4,pady=5,sticky="e")
        # ---------------------------------------------------------------------        
    
    def sel_Dictfile(self):
        fname = tkFileDialog.askopenfilename(parent=self,initialdir = op.join(getcwd(),self.vdatapath.get()),
                                        title = "Choix du dictionnaire MOTUS",initialfile=self.vdicofile.get(),
                                                      filetypes = (("Motus dico","*.txt"),("Tous types","*.*")))
        if fname:
            self.vdicofile.set(op.basename(fname))
    
    def sel_helpfile(self):
        fname = tkFileDialog.askopenfilename(parent=self, initialdir = op.join(getcwd(),self.vdatapath.get()),
                                        title = "Choix du fichier d'aide MOTUS",initialfile=self.vhelpfile.get(),
                                                        filetypes = (("Aide Motus","*.txt"),("Tous types","*.*")))
        if fname:
            self.vhelpfile.set(op.basename(fname))
    
    def sel_backfile(self):
        fname = tkFileDialog.askopenfilename(parent=self, initialdir = op.join(getcwd(),self.vimagepath.get()),
                                      title = "Choix de l'Image de fond d'écran",initialfile=self.vbackfile.get(),
                                        filetypes = (("Image PNG","*.png"),("Image GIF","*.gif"),("Tous types","*.*")))
        if fname:
            self.vbackfile.set(op.basename(fname))
    
    def sel_datapath(self):
        pname = tkFileDialog.askdirectory(initialdir = op.join(getcwd(),self.vdatapath.get()),
                                                parent = self, title = "Choix du dossier des données")
        if pname:
            self.vdatapath.set(op.basename(pname))
    
    def sel_imagepath(self):
        pname = tkFileDialog.askdirectory(initialdir=op.join(getcwd(),self.vimagepath.get()),
                                                        parent=self,title="Choix du dossier des images")
        if pname:
            self.vimagepath.set(op.basename(pname))
    
    def __valid_parameters(self, event=None) -> App_Options:
        # ---------------------------------------------------------------------
        index = gamemodelist.index(self.vgamemode.get())
        self.__parameters.gamemode = "human" if index == 0 else "computer" if index == 1 else "fighters"
        # ---------------------------------------------------------------------
        self.__parameters.fullscreen = bool(self.vfullscreen.get())
        self.__parameters.lettersound = bool(self.vlettersound.get())
        self.__parameters.musicgame = bool(self.vmusicgame.get())
        self.__parameters.soundgame = bool(self.vsoundgame.get())
        self.__parameters.difficulty = self.vdifficulty.get()
        self.__parameters.dicofilename = self.vdicofile.get()
        self.__parameters.helpfilename = self.vhelpfile.get()
        self.__parameters.backfilename = self.vbackfile.get()
        self.__parameters.accentchar = self.vaccentchar.get()
        self.__parameters.nb_letters = self.vnblettres.get()
        self.__parameters.imagepath = self.vimagepath.get()
        self.__parameters.dicopath = self.vdatapath.get()
        self.__parameters.nb_tries = self.vnbtries.get()
        # ---------------------------------------------------------------------
        return self.__parameters

    def go(self) -> App_Options:
        """ Methode qui permet de garder le focus sur la fenetre de choix
            de l'huile qui lors du choix renvoi le nom de l'huile choisie
            et ferme la fenetre Toplevel.
        """
        self.lift(self.__master)            # mise au premier plan de la Toplevel
        self.wait_visibility()
        self.grab_set() 
        self.how = self.__valid_parameters  # Nom de la procédure exécutée en sortie
        self.mainloop()                     # Sortie de la Boucle principale par "self.quit(how)"
        self.destroy()                      # Fermeture de la fenetre Toplevel
        return self.how
        
    def ok_command(self, event=None):
        self.Quit(self.__valid_parameters(event))
        
    def no_command(self, event=None):
        self.Quit(event)
        
    def Quit(self, how=None):
        """ Sortie de la boucle principale et non fermeture de la fenetre
            Exécution de la methode "how" qui permet de récupérer la
            donnée voulue en sotie en fin de méthode "go()"
        """
        self.how = how
        self.quit()                     # Exit mainloop()
        
    
# ----------------------------- Méthodes diverses -----------------------------    

def Select_Dictionary_File(master) -> str:
    fname = tkFileDialog.askopenfilename(parent=master, initialdir = op.join(getcwd(),"data"),
                      title = "Choix du dictionnaire MOTUS",initialfile=default_dico_filename,
                                    filetypes = (("Motus dico","*.txt"),("Tous types","*.*")))
    if fname:
        return fname

def get_widget(parent, pathname:str)->object:
    """ Retourne l'objet dont le nom est 'pathname' et qui appartient à 'parent' """
    try:
        widget = parent.nametowidget(pathname)
        return widget
    except KeyError:
        return None


if __name__ == "__main__":
    
    def do_Nothing():
        return None
        
    root = tk.Tk()
    root.app_size = root.maxsize()
    
    chrono = Chronometre(root, mode="normal")
    chrono.start_chrono()
    chrono.grid()
    
    #rules = Game_Rules(root,default_help_filename)
    # -------------------------------------------------------------------------
    #'commandsList': tuple de la forme (label_cmd:str, accel_cmd:str,commande:list[callable])
    #'nosel'       : list[int] liste des indices des rubrique dont l'état sera 'disabled'
    new_menu = [(" Paramètres","Ctrl-P",do_Nothing),(" Dictionnaire MOTUS","Ctrl-D",do_Nothing),
                (" Changer Mode de jeu","Ctrl-M",do_Nothing),("separator","",None),(" Quitter","",quit)]
    # -------------------------------------------------------------------------
    #menu = OneClick_CopyPaste(rules,rules.get_TextWidget(),new_menu, [6,7])
    #menu = Motus_PopupMenu(root,new_menu,[2,3,4])
    #rules.bind("<Button-3>", menu.show_Menu_Popup)
    #rules.show_helpfile()
    #rules.deiconify()
    #rules.lift(root)
    #msgbox = Win_MessageBox(root)
    #msgbox.message = "Win Message Box"
    #msgbox.lift(root)
    #message = f"\n{'Vous avez trouvé le mot MOTUS':100}\n{'Nouvelle partie ?':100}\n"
    #print(My_MessageBox(root,"Faites votre choix de partie",message,0).go())
    # -------------------------------------------------------------------------
    #dicofile = Select_Dictionary_File(root)
    config = Saveload_CFG()
    root.app_Parameters = config
    #print(f"Dico filename: {dicofile}")
    #if dicofile: config.options.dicofilename = op.basename(dicofile)
    # -------------------------------------------------------------------------
    difficulty = Difficulty_Popup(root).go()
    print(f"selected difficulty: {difficulty}")
    config.options.difficulty = difficulty[1]
    chrono.change_mode(difficulty[1])
    chrono.start_chrono()

    winparams = Parameters_Box(root,config.options).go()
    if winparams:     
        print(f"winparams:\n{winparams}")
    # -------------------------------------------------------------------------
    root.mainloop()
    
