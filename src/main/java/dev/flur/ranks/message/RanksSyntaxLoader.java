package dev.flur.ranks.message;

import io.pebbletemplates.pebble.loader.Loader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class RanksSyntaxLoader implements Loader<String> {

    private final Loader<String> delegate;

    public RanksSyntaxLoader(Loader<String> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Reader getReader(String templateName) {
        try {
            Reader r = delegate.getReader(templateName);
            String content = readAll(r);
            content = content
                    .replace("<<", "{{")
                    .replace(">>", "}}")
                    .replace("<[", "{% ")
                    .replace("]>", " %}");
            return new StringReader(content);
        } catch (Exception e) {
            throw new RuntimeException("Error loading template: " + templateName, e);
        }
    }

    private @NotNull String readAll(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public void setCharset(String charset) {
        delegate.setCharset(charset);
    }

    @Override
    public void setPrefix(String prefix) {
        delegate.setPrefix(prefix);
    }

    @Override
    public void setSuffix(String suffix) {
        delegate.setSuffix(suffix);
    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
        return delegate.resolveRelativePath(relativePath, anchorPath);
    }

    @Override
    public String createCacheKey(String templateName) {
        return delegate.createCacheKey(templateName);
    }

    @Override
    public boolean resourceExists(String templateName) {
        return delegate.resourceExists(templateName);
    }
}