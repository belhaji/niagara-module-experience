package com.belhaji.niagara.module.editor.action;

import com.intellij.psi.PsiElement;

/**
 * Simple list item
 */
public class SimpleCommentBlockListItem {
    private final PsiElement middleComment;

    public SimpleCommentBlockListItem(PsiElement middleComment) {
        this.middleComment = middleComment;
    }

    public PsiElement getMiddleComment() {
        return middleComment;
    }

    @Override
    public String toString() {
        return middleComment.getText();
    }
}
