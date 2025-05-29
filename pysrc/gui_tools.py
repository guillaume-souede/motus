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

DNA_GBRecords_GUI v2 (C) 2025  Bernard AMOUROUX
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

from os import getcwd
from configs import *

class My_LabelFrame(tk.LabelFrame):

    def __init__(self,master,col=0,row=0,cspan=1,rspan=1,pad=(0,0,0,0),sticky='nsew', *args, **kwargs):

        tab_options:dict = {'bg':'ivory', 'bd':3, 'relief':'groove', 'labelanchor':'n'}
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)

        tk.LabelFrame.__init__(self,master,*args,**kwargs)        

        self.grid(column=col, row=row, columnspan=cspan, rowspan=rspan,
                                  padx=pad[0], pady=pad[1], ipadx=pad[2], ipady=pad[3], sticky=sticky)
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
        tk.Label(self,bd=0,bg=self.cget('bg'),anchor="sw",height=1,textvariable=self.__vl_texte).grid()
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
        
        tk.Message(self,bg='wheat',width=400,aspect=100,justify=tk.CENTER,
                                           textvariable=self.__vmessage).grid(padx=10,pady=10)
        tk.Button(self,width = 8,bg='tan',text='Ok',command=self.Quit).grid()
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
        tab_action = [(" Rejouer "," Quitter "),("Oui","Non")]
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':3,'bg':'wheat','relief':'ridge','name':"!my_MessageBox"}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        # ---------------------------------------------------------------------        
        self.protocol("WM_DELETE_WINDOW", self.choose_cancel)
        msg_font = ('Courier\ New 18 bold italic')
        btn_font = ('Courier\ New 14 bold italic')
        self.bind("<Escape>", self.no_command)
        self.bind("<Return>", self.ok_command)
        self.title(self.__vtitle.get())
        self.resizable(False, False)
        # ---------------------------------------------------------------------
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

    def valide_ok_command(self, event):
        print(f"event: {event}")

    def go(self):
        """ Methode qui permet de garder le focus sur la fenetre de choix
            de l'huile qui lors du choix renvoi le nom de l'huile choisie
            et ferme la fenetre Toplevel.
        """
        self.lift(self.__master)        # mise au premier plan de la Toplevel    
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
        
    def __init__(self, master, filename:str=None, *args, **kwargs):
         
        self.__master = master
        self.__filename = filename if filename else op.join(getcwd(),dico_path,default_help_filename)
        self.__vtitle = tk.StringVar(value="Aide de MOTUS v1.0.10 (c)AMOUROUX Bernard 05/2025")
        self.__dico_lines:dict[int:str] = ({})
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':3,'bg':'ivory','relief':'ridge','name':"!my_gameRules"}        
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
    
    def show_helpfile(self):
        self.create_widgets()
        
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
    
    def create_widgets(self):
        # ------------------ Tags à placer dans le tk.Text() ------------------
        text_tags = [(" Carré Rouge  ","ok",COLOR_OK),
                     (" Carré Bleu   ","is",COLOR_IS),
                     (" Carré Jaune  ","no",COLOR_NO),       ]
        # ---------------------------------------------------------------------
        versb = tk.Scrollbar(self, orient=tk.VERTICAL)
        versb.grid(column=1,row=0,sticky='nse')
        #self.text_help = tk.Text(self,bg='ivory',bd=0,font=self.txt_font,wrap="word",relief="flat",width=90)
        self.__text_help.grid(column=0,row=0,padx=10,pady=10,sticky="nsew")
        self.__text_help['yscrollcommand'] = versb.set   
        versb['command'] = self.__text_help.yview
        # ------------ Insertion du texte dans le widget tk.Text() ------------        
        [self.__text_help.insert(tk.END, f"{line}\n") for line in self.__dico_lines.values()]
        # ---------------------------------------------------------------------
        self.__look_for_tags(self.__text_help.get("1.0", tk.END),text_tags)
        self.__text_help.configure(state="disabled")
    
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
        self.destroy()


class OneClick_CopyPaste():
    """ Classe Menu popup copier/coller couper.
        Les parametres du contructeur sont:
            'master': widget appelant dont le parent est un tk.Toplevel()
            'widget': le widget tk.Text() de l'appelant.
        Les 2 paramètres suivants sont optionnels et non nécessaire pour 'Copier/Coller Couper' !
            'commandsList': tuple de la forme (label_cmd:str, accel_cmd:str,commande:list[callable])
            'nosel' : list[int] liste des indices des rubrique dont l'état sera 'disabled'
    """
    def __init__(self, master:object, widget:tk.Text, commandsList:tuple=None, nosel:list=None):
        
        self.__master = master
        self.__widget = widget
        self.__commandList = commandsList
        self.__NoSel = [2, 3]
        if nosel : self.__NoSel.extend(nosel)
        # ----- Pour pouvoir Couper/Coller dans le widget Text() pour test ----
        self.master = master.get_Parent()
            
    def show_Menu_Popup(self, event:tk.Event):
        
        menu_Font = ('Arial 12 bold italic')
        # ---------------------------------------------------------------------
        def nomenupopup(nosel:list):                
            [popup_menu.entryconfigure(i, state = 'disabled') for i in nosel \
                                                      if not self.__widget.tag_ranges('sel')]
        # ---------------------------------------------------------------------
        #def add_commandsList(commandlist:list):
        #    [popup_menu.add_command(label=cmd[0],accelerator=cmd[1],command=cmd[2]) for cmd in commandlist]
        # ---------------------------------------------------------------------
        popup_menu = tk.Menu(self.master,tearoff=0,font=menu_Font,postcommand=lambda :nomenupopup(self.__NoSel))
        popup_menu.add_command(label="  OneClick Copy/Paste",accelerator ="and Cut ",
                                                        background='orange',activebackground='orange')
        popup_menu.add_separator()
        popup_menu.add_command(label="Copier",accelerator="Ctrl-C",command=self.__menucopier,activebackground='tan')
        popup_menu.add_command(label="Couper",accelerator="Ctrl-X",command=self.__menucouper,activebackground='tan')
        popup_menu.add_command(label="Coller",accelerator="Ctrl-V",command=self.__menucoller,activebackground='tan')
        popup_menu.add_separator()
        if self.__commandList: 
            #add_commandsList(self.__commandList) 
            [popup_menu.add_command(label=cmd[0],accelerator=cmd[1],command=cmd[2]) for cmd in self.__commandList]
        # ---------------------------------------------------------------------
        try:
            popup_menu.tk_popup(event.x_root, event.y_root)
            self.__widget.configure(state='normal')
        except Exception as e:
            print(f"Erreur interne : {e}")
            popup_menu.grab_release()
                
    def __menucoller(self, event=None):
        try:
            texttopaste = self.master.clipboard_get()
            [self.__widget.insert(tk.INSERT, w) for w in texttopaste]
        except:
            print("Rien à coller/Texte en lecture seule")
            return

    def __menucopier(self, event=None)-> bool:        
        self.master.clipboard_clear()
        idx = self.__widget.tag_ranges('sel')
        if (idx):
            self.master.clipboard_append(self.__widget.selection_get())
            return True
        return False

    def __menucouper(self, event=None):
        if self.__menucopier() and self.__widget.cget('state') != 'disabled':
            self.__widget.delete('sel.first', 'sel.last')
        else:
            print("Rien à couper/Texte en lecture seule")
    
# ----------------------------- Méthodes diverses -----------------------------    
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
    rules = Game_Rules(root)
    # -------------------------------------------------------------------------
    #'commandsList': tuple de la forme (label_cmd:str, accel_cmd:str,commande:list[callable])
    #'nosel'       : list[int] liste des indices des rubrique dont l'état sera 'disabled'
    new_menu = [("copy","Ctrl-C",do_Nothing),("Paste","Ctrl-V",do_Nothing),("Cut","Ctrl-X",do_Nothing)]
    # -------------------------------------------------------------------------
    menu = OneClick_CopyPaste(rules,rules.get_TextWidget(),new_menu, [6,7])
    rules.bind("<Button-3>", menu.show_Menu_Popup)
    rules.show_helpfile()
    #msgbox = Win_MessageBox(root)
    #msgbox.message = "Win Message Box"
    #msgbox.lift(root)
    #message = f"\n{'Vous avez trouvé le mot MOTUS':100}\n{'Nouvelle partie ?':100}\n"
    #print(My_MessageBox(root,"Faites votre choix de partie",message,0).go())
    root.mainloop()
