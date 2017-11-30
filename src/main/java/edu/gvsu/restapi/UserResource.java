package edu.gvsu.restapi;

import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.Delete;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Key;


/**
 * Represents a collection of widgets.  This resource processes HTTP requests that come in on the URIs
 * in the form of:
 * <p>
 * http://host:port/widgets/{id}
 * <p>
 * This resource supports both HTML and JSON representations.
 *
 * @author Jonathan Engelsma (http://themobilemontage.com)
 */
public class UserResource extends ServerResource {

    private User user = null;

    @Override
    public void doInit() {

        // URL requests routed to this resource have the user name on them.
        String username = null;
        username = (String) getRequest().getAttributes().get("name");

        // lookup the user in google's persistance layer.
        Key<User> theKey = Key.create(User.class, username);
        this.user = ObjectifyService.ofy()
                .load()
                .key(theKey)
                .now();

        // these are the representation types this resource supports.
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    }

    /**
     * Represent the user object in the requested format.
     *
     * @param variant
     * @return
     * @throws ResourceException
     */
    @Get
    public Representation get(Variant variant) throws ResourceException {
        Representation result;
        if (null == this.user) {
            ErrorMessage em = new ErrorMessage();
            result = representError(variant.getMediaType(), em);
            getResponse().setEntity(result);
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return result;
        } else {
            if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
                result = new JsonRepresentation(this.user.toJSON());
                result.setMediaType(MediaType.APPLICATION_JSON);
            } else {
                result = new StringRepresentation(this.user.toHtml(false));
                result.setMediaType(MediaType.TEXT_HTML);
            }
        }
        return result;
    }

    /**
     * Handle a PUT Http request. Update an existing user
     *
     * @param entity
     * @throws ResourceException
     */
    @Put
    public Representation put(Representation entity, Variant variant) throws ResourceException {
        Representation rep = null;
        try {
            if (null == this.user) {
                ErrorMessage em = new ErrorMessage();
                rep = representError(entity.getMediaType(), em);
                getResponse().setEntity(rep);
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return rep;
            }
            if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM, true)) {
                Form form = new Form(entity);
                this.user.setName(form.getFirstValue("name"));
                this.user.setIpAddress(form.getFirstValue("ipAddress"));
                this.user.setPort(form.getFirstValue("port"));

                // persist object
                ObjectifyService.ofy()
                        .save()
                        .entity(this.user)
                        .now();

                getResponse().setStatus(Status.SUCCESS_OK);
                rep = new JsonRepresentation(this.user.toJSON());
                getResponse().setEntity(rep);
            } else if (entity.getMediaType().equals(MediaType.APPLICATION_JSON, true)) {
                JSONObject requestBody = new JSONObject(entity);
                JSONObject body = new JSONObject(requestBody.get("text").toString());
                Boolean status = Boolean.parseBoolean(body.get("status").toString());
                this.user.setStatus(status);

                ObjectifyService.ofy()
                        .save()
                        .entity(this.user)
                        .now();

                getResponse().setStatus(Status.SUCCESS_OK);
                rep = new JsonRepresentation(this.user.toJSON());
                getResponse().setEntity(rep);

            } else {
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        } catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return rep;
    }

    /**
     * Handle a DELETE Http Request. Delete an existing user
     *
     * @param variant
     * @throws ResourceException
     */
    @Delete
    public Representation delete(Variant variant)
            throws ResourceException {
        Representation rep = null;
        try {
            if (null == this.user) {
                ErrorMessage em = new ErrorMessage();
                rep = representError(MediaType.APPLICATION_JSON, em);
                getResponse().setEntity(rep);
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return rep;
            }

            try {
                rep = new JsonRepresentation(this.user.toJSON());

                // remove from persistance layer
                ObjectifyService.ofy()
                        .delete()
                        .entity(this.user);

            } finally {

            }

            getResponse().setStatus(Status.SUCCESS_OK);
        } catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return rep;
    }

    /**
     * Represent an error message in the requested format.
     *
     * @param variant
     * @param em
     * @return
     * @throws ResourceException
     */
    private Representation representError(Variant variant, ErrorMessage em)
            throws ResourceException {
        Representation result = null;
        if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
            result = new JsonRepresentation(em.toJSON());
        } else {
            result = new StringRepresentation(em.toString());
        }
        return result;
    }

    protected Representation representError(MediaType type, ErrorMessage em)
            throws ResourceException {
        Representation result = null;
        if (type.equals(MediaType.APPLICATION_JSON)) {
            result = new JsonRepresentation(em.toJSON());
        } else {
            result = new StringRepresentation(em.toString());
        }
        return result;
    }
}
