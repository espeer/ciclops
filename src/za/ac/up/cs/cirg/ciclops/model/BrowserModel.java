/*
 * BrowserModel.java
 *
 * Created on March 24, 2004, 1:55 PM
 */

package za.ac.up.cs.cirg.ciclops.model;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import za.ac.up.cs.cirg.ciclops.client.ServerContext;
import za.ac.up.cs.cirg.ciclops.client.UnexpectedError;
import za.ac.up.cs.cirg.ciclops.services.CategoryBrowser;

/**
 *
 * @author  espeer
 */
public class BrowserModel implements TreeModel, TreeSelectionModel, ListModel {
    

    
    /** Creates a new instance of BrowserModel */
    public BrowserModel(int type) {
        try {
            browser = ServerContext.instance().getCategoryBrowserHome().create();
        }
        catch (Exception ex) {
            throw new UnexpectedError(ex);
        }
        
        this.type = type;
        
        propertyChangeListeners = new LinkedList();
        treeModelListeners = new LinkedList();
        treeSelectionListeners = new LinkedList();
       
        list = null;
        listDataListeners = new LinkedList();
        root = new Category(BrowserItem.CATEGORY_ROOT);
        selection = new TreePath(root);
    }
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        propertyChangeListeners.add(propertyChangeListener);
    }
    
    public void addSelectionPath(javax.swing.tree.TreePath path) {
        if (path != null) {
            setSelectionPath(path);
        }
    }
    
    public void addSelectionPaths(javax.swing.tree.TreePath[] paths) {
        if (paths[0] != null) {
            setSelectionPath(paths[0]);
        }
    }
    
    public void addTreeModelListener(javax.swing.event.TreeModelListener treeModelListener) {
        treeModelListeners.add(treeModelListener);
    }
    
    public void addTreeSelectionListener(javax.swing.event.TreeSelectionListener treeSelectionListener) {
        treeSelectionListeners.add(treeSelectionListener);
    }
    
    public void clearSelection() {
        setSelectionPath(new TreePath(root));
    }
    
    public Object getChild(Object obj, int index) {
        return ((Category) obj).getChild(index);
    }
    
    public int getChildCount(Object obj) {
        return ((Category) obj).getChildCount();
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        else {
            return ((Category) parent).getIndex((Category) child);
        }
    }
    
    public javax.swing.tree.TreePath getLeadSelectionPath() {
        return selection;
    }
    
    public int getLeadSelectionRow() {
        return -1;
    }
    
    public int getMaxSelectionRow() {
        return -1;
    }
    
    public int getMinSelectionRow() {
        return -1;
    }
    
    public Object getRoot() {
        return root;
    }
    
    public javax.swing.tree.RowMapper getRowMapper() {
        return null;
    }
    
    public int getSelectionCount() {
        return 1;
    }
    
    public int getSelectionMode() {
        return TreeSelectionModel.SINGLE_TREE_SELECTION; 
    }
    
    public javax.swing.tree.TreePath getSelectionPath() {
        return selection;
    }
    
    public javax.swing.tree.TreePath[] getSelectionPaths() {
        return new TreePath[] { selection };
    }
    
    public int[] getSelectionRows() {
        return new int[] { -1 };
    }
    
    public boolean isLeaf(Object obj) {
        return false;
        // Category category = (Category) obj;
        // return category.getChildCount() == 0;
    }
    
    public boolean isPathSelected(javax.swing.tree.TreePath path) {
        return path.equals(selection);
    }
    
    public boolean isRowSelected(int param) {
        return false;
    }
    
    public boolean isSelectionEmpty() {
        return selection == null;
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        propertyChangeListeners.remove(propertyChangeListener);
    }
    
    public void removeSelectionPath(javax.swing.tree.TreePath path) {
        if (path != null && path.equals(selection)) {
            setSelectionPath(new TreePath(root));
        }
    }
    
    public void removeSelectionPaths(javax.swing.tree.TreePath[] paths) {
        for (int i = 0; i < paths.length; ++i) {
            if (paths[i] != null && paths[i].equals(selection)) {
                setSelectionPath(new TreePath(root));
                return;
            }
        }
    }
    
    public void removeTreeModelListener(javax.swing.event.TreeModelListener treeModelListener) {
        treeModelListeners.remove(treeModelListener);
    }
    
    public void removeTreeSelectionListener(javax.swing.event.TreeSelectionListener treeSelectionListener) {
        treeSelectionListeners.remove(treeSelectionListeners);
    }
    
    public void resetRowSelection() {
    }
    
    public void setRowMapper(javax.swing.tree.RowMapper rowMapper) {
    }
    
    public void setSelectionMode(int param) {
        throw new UnsupportedOperationException();
    }
    
    public void setSelectionPath(javax.swing.tree.TreePath path) {
        if (selection != path) {
            TreePath old = selection;
            selection = path;
            list = null;
            for (Iterator i = treeSelectionListeners.iterator(); i.hasNext(); ) {
                TreeSelectionListener listener = (TreeSelectionListener) i.next();
                listener.valueChanged(new TreeSelectionEvent(this, selection, true, old, selection));
            }
            fireListContentsChanged();
        }
    }
    
    private void fireListContentsChanged() {
        for (Iterator i = listDataListeners.iterator(); i.hasNext(); ) {
           ListDataListener listener = (ListDataListener) i.next();
           listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 1));
        }
    }

    public void setSelectionPaths(javax.swing.tree.TreePath[] paths) {
        setSelectionPath(paths[0]);
    }
    
    public void valueForPathChanged(javax.swing.tree.TreePath path, Object obj) {
    }
    
    public void addListDataListener(javax.swing.event.ListDataListener listDataListener) {
        listDataListeners.add(listDataListener);
    }
    
    public Object getElementAt(int index) {
        updateList();
        return list.get(index);
    }
    
    public int getSize() {
        updateList();
        if (list == null) {
            return 0;
        }
        else {
            return list.size();
        }
    }
    
    private void updateList() {
        if (selection == null) {
            return;
        }
        Category category = (Category) selection.getLastPathComponent();
        if (list == null || category.isStale()) {
            list = new ArrayList();
            try {
            	list.addAll(browser.getChildren(type, category.getId()));
            }
            catch (RemoteException ex) {
            	throw new UnexpectedError(ex);
            }
        }
    }
     
    public BrowserItem getSelectedCategory() {
        Category category = (Category) selection.getLastPathComponent();
        return category.getData();
    }
    
    public void removeListDataListener(javax.swing.event.ListDataListener listDataListener) {
        listDataListeners.remove(listDataListener);
    }

    public void refresh() {
        root.refresh();
        list = null;
        fireListContentsChanged();
    }
    
    private void fireTreeNodeChanged(TreePath path) {
        for (Iterator i = treeModelListeners.iterator(); i.hasNext(); ) {
            TreeModelListener listener = (TreeModelListener) i.next();
            listener.treeNodesChanged(new TreeModelEvent(this, path));
        }
    }
    
    private void fireTreeNodesInserted(TreePath path, int[] childIndices, Object[] children) {
        for (Iterator i = treeModelListeners.iterator(); i.hasNext(); ) {
            TreeModelListener listener = (TreeModelListener) i.next();
            listener.treeNodesInserted(new TreeModelEvent(this, path, childIndices, children));
        }
    }
    
    private void fireTreeNodesRemoved(TreePath path, int[] childIndices, Object[] children) {
        for (Iterator i = treeModelListeners.iterator(); i.hasNext(); ) {
            TreeModelListener listener = (TreeModelListener) i.next();
            listener.treeNodesRemoved(new TreeModelEvent(this, path, childIndices, children));
        }
    }
    
    private void fireTreeStructureChanged(TreePath path) {
        for (Iterator i = treeModelListeners.iterator(); i.hasNext(); ) {
            TreeModelListener listener = (TreeModelListener) i.next();
            listener.treeStructureChanged(new TreeModelEvent(this, path));
        }
    }
    
    public void createCategory(String name) {
        if (selection == null) {
            return;
        }
        Category current = (Category) selection.getLastPathComponent();
        try {
            browser.createCategory(current.getId(), name);
            current.clear();
            list = null;
            fireTreeStructureChanged(selection);
            fireListContentsChanged();
        }
        catch (Exception ex) {
            throw new UnexpectedError(ex);
        }
    }
    
    private Collection propertyChangeListeners;
    private Collection treeModelListeners;
    private Collection treeSelectionListeners;
    private Collection listDataListeners;
    
    private List list;
    
    private int type;
    
    private TreePath selection;
    
    private Category root;
    
    private CategoryBrowser browser;
    
    private class Category {
        
        public Category(int categoryId) {
           this.categoryId = categoryId;
           data = null;
           children = null;
           accessTime = System.currentTimeMillis();
           path = new TreePath(this);
        }
        
        private Category(TreePath parent, BrowserItem data) {
            this.data = data;
            this.categoryId = data.getId();
            children = null;
            path = parent.pathByAddingChild(this);
            accessTime = System.currentTimeMillis();
        }
        
        public void clear() {
            updateAccessTime();
            this.data = null;
            this.children = null;
        }
        
        public int getId() {
            return categoryId;
        }
        
        public int getChildCount() {
            updateAccessTime();
            
            if (children == null) {
                children = fetchChildren();
            }
            
            if (children == null) {
                return 0;
            }
            else {
                return children.size();
            }  
        }
        
        public Category getChild(int index) {
            updateAccessTime();
            if (children == null) {
                children = fetchChildren();
            }
            return (Category) children.get(index);
        }
        
        public int getIndex(Category child) {
            updateAccessTime();
            return children.indexOf(child);
        }
        
        public boolean isStale() {
            return (System.currentTimeMillis() - accessTime) > 120000;
        }
        
        public String toString() {
            return getData().getName();
        }
        
        public BrowserItem getData() {
            updateAccessTime();
            if (data == null) {
                data = fetchData();
            }
            return data;    
        }
        
        private BrowserItem fetchData() {
            try {
                return browser.getCategory(categoryId);
            }
            catch (RemoteException ex) {
                throw new UnexpectedError(ex);
            }
        }
        
        private List fetchChildren() {
            try {
                List list = new ArrayList();
                Iterator i = browser.getChildren(BrowserItem.TYPE_CATEGORY, categoryId).iterator();
                while (i.hasNext()) {
                    BrowserItem tmp = (BrowserItem) i.next();
                    list.add(new Category(path, tmp));
                }  
                return list;
            }
            catch (RemoteException ex) {
                throw new UnexpectedError(ex);
            }
        }
        
        public void refresh() {
            if (isStale()) {
                clear();
                fireTreeStructureChanged(path);
            }
            else {
                if (data == null) {
                    return;
                }
                
                BrowserItem tmp = fetchData();
                if (! data.getName().equals(tmp.getName())) {
                    data = tmp;
                    fireTreeNodeChanged(path);
                }
                
                if (children == null) {
                    return;
                }
                
                List tmpChildren = fetchChildren();
                
                boolean modified = false;
                
                if (children.retainAll(tmpChildren)) {
                    modified = true;
                }
                
                for (Iterator i = children.iterator(); i.hasNext(); ) {
                    ((Category) i.next()).refresh();
                }
                
                tmpChildren.removeAll(children);
                if (children.addAll(tmpChildren)) {
                    modified = true;
                }
                
                if (modified) {                   
                    // TODO: Fire TreeNodesInserted and TreeNodesRemoved instead - will need to change how we add and remove stuff above to do this 
                    fireTreeStructureChanged(path);
                }
                
            }
        }
        
        private void updateAccessTime() {
            accessTime = System.currentTimeMillis();
            if (path.getPath()[0] == this) {
                return;
            }
            Object[] ancestors = path.getParentPath().getPath();
            for (int i = 0; i < ancestors.length; ++i) {
                ((Category) ancestors[i]).accessTime = accessTime;
            }
        }
        
        public boolean equals(Object other) {
            return ((Category) other).categoryId == categoryId;
        }
        
        public int hashCode() {
            return categoryId;
        }
        
        private BrowserItem data;
        private List children;
        private long accessTime;
        private int categoryId;
        private TreePath path;
    }
}
