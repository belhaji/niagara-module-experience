package com.belhaji.niagara.module.editor.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.HighlightableCellRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This action adds navigation between niagara blocks of comment
 */
public class CommentBlockNavigationAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        if (project != null) {
            PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
            if (psiFile != null && psiFile.getLanguage().getID().equals("JAVA")) {
                e.getPresentation().setEnabledAndVisible(true);
                return;
            }
        }

        e.getPresentation().setEnabledAndVisible(false);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("Navigate activated");

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        List<SimpleCommentBlockListItem> commentBlocks = findCommentBlocks(psiFile).stream().map(SimpleCommentBlockListItem::new).collect(Collectors.toList());

        IPopupChooserBuilder<SimpleCommentBlockListItem> popupChooserBuilder = JBPopupFactory.getInstance()
                .createPopupChooserBuilder(commentBlocks)
                .setTitle("Choose a Block")
                .setRenderer(new HighlightableCellRenderer())
                .setItemSelectedCallback(item -> {
                    if (item == null) return;
                    Editor editor = e.getData(CommonDataKeys.EDITOR);
                    if (editor != null) {
                        editor.getCaretModel().removeSecondaryCarets();
                        editor.getCaretModel().moveToOffset(item.getMiddleComment().getTextOffset() + 3);
                        editor.getSelectionModel().selectWordAtCaret(true);
                        ScrollingModel scrollingModel = editor.getScrollingModel();
                        scrollingModel.disableAnimation();
                        scrollingModel.scrollToCaret(ScrollType.CENTER);
                        scrollingModel.enableAnimation();
                    }
                })
                .setItemChosenCallback(item -> {
                    if (item == null) return;
                    Editor editor = e.getData(CommonDataKeys.EDITOR);
                    if (editor != null) {
                        editor.getSelectionModel().removeSelection();
                    }
                });

        popupChooserBuilder.createPopup().showInFocusCenter();
    }

    /**
     * Search for comment blocks
     *
     * @param psiFile the current psi file
     * @return list of psi middle comment of each block
     */
    private List<PsiElement> findCommentBlocks(PsiFile psiFile) {
        if (psiFile == null) {
            return Collections.emptyList();
        }
        // List of the starting comment of the block
        List<PsiComment> blockComments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment.class).stream().filter(this::isStartOfBlockComment).collect(Collectors.toList());
        return blockComments.stream().map(this::getMiddleComment).collect(Collectors.toList());
    }

    /**
     * Gets the middle comment element of a block comment
     *
     * @param element the starting element of a block comment
     * @return the middle element of the block
     */
    private PsiElement getMiddleComment(PsiElement element) {
        // Skip the white space element
        return element.getNextSibling().getNextSibling();
    }

    /**
     * Checks if the element is a starting of a block comment
     *
     * @param comment the psi comment to be checked
     * @return true if the psi comment is the start of a block comment
     */
    private boolean isStartOfBlockComment(PsiComment comment) {
        if (comment.getText() != null && comment.getText().matches("///+") && comment.getNextSibling() instanceof PsiWhiteSpace) {
            PsiElement whiteSpace = comment.getNextSibling();
            if (whiteSpace.getNextSibling() instanceof PsiComment) {
                PsiComment innerComment = (PsiComment) whiteSpace.getNextSibling();
                if (innerComment.getText() != null && innerComment.getText().matches("//.*") && innerComment.getNextSibling() instanceof PsiWhiteSpace) {
                    PsiElement whiteSpace2 = innerComment.getNextSibling();
                    if (whiteSpace2.getNextSibling() instanceof PsiComment) {
                        PsiComment endBlockComment = (PsiComment) whiteSpace2.getNextSibling();
                        return endBlockComment.getText() != null && endBlockComment.getText().matches("///+");
                    }
                }
            }
        }
        return false;
    }
}
