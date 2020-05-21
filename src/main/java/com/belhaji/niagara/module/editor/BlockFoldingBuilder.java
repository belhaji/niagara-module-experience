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

public class BlockFoldingBuilder extends FoldingBuilderEx implements DumbAware
{

    @Override
    public @NotNull FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick)
    {
        // Initialize the group of folding regions that will expand/collapse together.
        FoldingGroup group = FoldingGroup.newGroup("BAJA_GENERATED_CODE");
        // Initialize the list of folding regions
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<PsiComment> commentCollection = PsiTreeUtil.findChildrenOfType(root, PsiComment.class);
        PsiComment startComment = null;
        PsiComment endComment = null;
        for (PsiComment comment : commentCollection)
        {
            String value = comment.getText();
            if (value != null && value.equals("/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/"))
            {
                startComment = comment;
            }
            else if (value != null && value.equals("/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/"))
            {
                endComment = comment;
            }
            if (startComment != null && endComment != null)
            // Add a folding descriptor for the literal expression at this node.
            {
                descriptors.add(new FoldingDescriptor(startComment.getNode(),
                                                      new TextRange(startComment.getTextRange().getStartOffset(),
                                                                    endComment.getTextRange().getEndOffset()),
                                                      null));
                startComment = null;
                endComment = null;
            }
        }
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node)
    {
        return true;
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node)
    {
        return "BAJA GENCODE ...";
    }
}
