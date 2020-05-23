package com.belhaji.niagara.module.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlockFoldingBuilder extends FoldingBuilderEx implements DumbAware {
    public static final String BEGIN_BAJA_AUTO_GENERATED_CODE = "/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/";
    public static final String END_BAJA_AUTO_GENERATED_CODE = "/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/";

    @Override
    public @NotNull FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup("BAJA_GENERATED_CODE");
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<PsiComment> commentCollection = PsiTreeUtil.findChildrenOfType(root, PsiComment.class);
        PsiComment startComment = null;
        PsiComment endComment = null;
        for (PsiComment comment : commentCollection) {
            String value = comment.getText();
            if (value != null && value.equals(BEGIN_BAJA_AUTO_GENERATED_CODE)) {
                startComment = comment;
            } else if (value != null && value.equals(END_BAJA_AUTO_GENERATED_CODE)) {
                endComment = comment;
            }
            if (startComment != null && endComment != null) {
                descriptors.add(new FoldingDescriptor(startComment.getNode(), new TextRange(startComment.getTextRange().getStartOffset(),
                        endComment.getTextRange().getEndOffset()), null));
                startComment = null;
                endComment = null;
            }
        }
        return descriptors.toArray(new FoldingDescriptor[]{});
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        return "BAJA GENCODE ...";
    }
}
